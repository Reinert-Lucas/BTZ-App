<?php

namespace App\Services;

use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Kreait\Firebase\Exception\Messaging\NotFound;
use Throwable;

class AvisoService
{
    public function __construct(
        protected FirebaseNotificationService $firebaseNotificationService
    ) {
    }
    /**
     * @param Aviso|null $aviso 
     * Al editar, se pasa el aviso para que su operario/cliente actual aparezcan en el select aunque estén dados de baja.
     */
    public function getFields(bool $isUpdate, ?Aviso $aviso = null)
    {
        $clientes = Cliente::withTrashed()
            ->where(fn($q) => $q->whereNull('deleted_at')
                ->when($aviso, fn($q2) => $q2->orWhere('cliente_id', $aviso->cliente_id)))
            ->pluck('nombre', 'cliente_id');
        $operarios = Usuario::query()
            ->withTrashed()
            ->where('rol', 'operario')
            ->where(fn($q) => $q->whereNull('deleted_at')
                ->when($aviso, fn($q2) => $q2->orWhere('usuario_id', $aviso->usuario_id)))
            ->pluck('nombre', 'usuario_id');
        $inputs = [
            ['label' => 'Fecha', 'field' => 'fecha', 'type' => 'date'],
            ['label' => 'Hora', 'field' => 'hora', 'type' => 'time'],
            ['label' => 'Direccion', 'field' => 'direccion', 'type' => 'text'],
            ['label' => 'Telefono', 'field' => 'telefono', 'type' => 'tel'],
            ['label' => 'Mensaje', 'field' => 'mensaje', 'type' => 'textarea'],
            ['label' => 'Observacion', 'field' => 'observacion', 'type' => 'textarea'],
            [
                'label' => 'Urgencia',
                'field' => 'urgencia',
                'type' => 'select',
                'options' => [
                    'urgente' => 'Urgente',
                    'media' => 'Media',
                    'baja' => 'Baja',
                ],
            ],
            ['label' => 'Cliente', 'field' => 'cliente_id', 'type' => 'select', 'options' => $clientes],
            ['label' => 'Operario', 'field' => 'usuario_id', 'type' => 'select', 'options' => $operarios],
        ];
        if ($isUpdate) {
            $inputs[] = [
                'label' => 'Estado',
                'field' => 'estado',
                'type' => 'select',
                'options' => [
                    'pendiente' => 'Pendiente',
                    'finalizado' => 'Finalizado',
                    'cancelado' => 'Cancelado',
                ],
            ];
        }
        return $inputs;
    }
    public function index(Request $request)
    {
        $usuarioAutenticado = Auth::user();
        $avisos = Aviso::query()
            ->with(['usuario', 'cliente', 'trabajo.materiales']);
        if ($usuarioAutenticado?->rol !== 'admin') {
            $avisos->where('usuario_id', $usuarioAutenticado?->usuario_id);
        }
        $avisos
            ->when($request->filled('buscar'), function ($query) use ($request) {
                $termino = trim($request->input('buscar'));
                $query->where(function ($subquery) use ($termino) {
                    $subquery
                        ->where('direccion', 'like', "%{$termino}%")
                        ->orWhere('telefono', 'like', "%{$termino}%");

                    if (ctype_digit($termino)) {
                        $subquery->orWhere('aviso_id', (int) $termino);
                    }
                });
            })
            ->when($request->filled('aviso_id'), function ($query) use ($request) {
                $query->where('aviso_id', $request->input('aviso_id'));
            })
            ->when($request->filled('fecha'), function ($query) use ($request) {
                $query->where('fecha', $request->input('fecha'));
            })
            ->when($request->filled('hora'), function ($query) use ($request) {
                $query->where('hora', $request->input('hora'));
            })
            ->when($request->filled('direccion'), function ($query) use ($request) {
                $query->where('direccion', 'like', '%' . $request->input('direccion') . '%');
            })
            ->when($request->filled('telefono'), function ($query) use ($request) {
                $query->where('telefono', 'like', '%' . $request->input('telefono') . '%');
            })
            ->when($request->filled('estado'), function ($query) use ($request) {
                $query->where('estado', $request->input('estado'));
            })
            ->when($request->filled('urgencia'), function ($query) use ($request) {
                $query->where('urgencia', $request->input('urgencia'));
            });
        return $this->ordenarAvisos($avisos)
            ->paginate(10)
            ->withQueryString();
    }
    /** Ordenar los avisos [Fecha -> Hora -> Urgencia] */
    private function ordenarAvisos($query)
    {
        return $query
            ->orderBy('fecha')
            ->orderBy('hora')
            ->orderByRaw("
                CASE urgencia
                    WHEN 'urgente' THEN 1
                    WHEN 'media' THEN 2
                    WHEN 'baja' THEN 3
                    ELSE 4
                END
            ");
    }
    public function show(int $aviso)
    {
        return Aviso::findOrFail($aviso);
    }
    public function create(array $avisoValidado)
    {
        $avisoValidado['estado'] = 'pendiente';
        $aviso = Aviso::create($avisoValidado);
        $this->notificarAsignacion($aviso);
        return $aviso;
    }
    public function update(array $newAviso, Aviso $aviso)
    {
        $aviso = DB::transaction(function () use ($newAviso, $aviso) {
            // Mismo lock que TrabajoController::store: si el operario está cargando el
            // trabajo en este instante, uno espera al otro y no queda un aviso
            // "cancelado" que ya tiene trabajo cargado.
            $actual = Aviso::whereKey($aviso->getKey())->lockForUpdate()->firstOrFail();
            $nuevoEstado = $newAviso['estado'] ?? $actual->estado;
            $tieneTrabajo = $actual->trabajo()->exists();
            abort_if(
                $nuevoEstado === 'finalizado' && !$tieneTrabajo,
                409,
                'Un aviso solo pasa a finalizado cuando el operario carga el trabajo.'
            );
            abort_if(
                $tieneTrabajo && $nuevoEstado !== 'finalizado',
                409,
                'No se puede reabrir un aviso que ya tiene un trabajo cargado.'
            );
            $actual->update($newAviso);
            return $actual;
        });
        // La notificación se envía recién cuando la transacción ya hizo commit.
        if ($aviso->wasChanged('usuario_id') && $aviso->estado === 'pendiente') {
            $this->notificarAsignacion($aviso);
        }
        return $aviso->fresh();
    }
    public function destroy(int $aviso): bool
    {
        return DB::transaction(function () use ($aviso) {
            $avisoObtenido = Aviso::lockForUpdate()->findOrFail($aviso);
            // Borrar el aviso dejaría el trabajo (y sus materiales) contando en las
            // métricas del dashboard sin un aviso visible. Se conserva el historial.
            abort_if(
                $avisoObtenido->trabajo()->exists(),
                409,
                'No se puede eliminar un aviso con trabajo cargado (se conserva el historial).'
            );
            return (bool) $avisoObtenido->delete();
        });
    }
    /** Se notifica al operario de manera asyncrona para no romper la Request */
    private function notificarAsignacion(Aviso $aviso): void
    {
        $usuario = $aviso->usuario;
        if (!$usuario?->fcm_token) {
            return;
        }
        defer(function () use ($aviso, $usuario) {
            try {
                $this->firebaseNotificationService->sendToToken(
                    $usuario->fcm_token,
                    '✅ Nuevo Aviso Asignado',
                    'Se te ha asignado un nuevo aviso. Toca para ver los detalles.',
                    ['aviso_id' => (string) $aviso->aviso_id]
                );
            } catch (NotFound) {
                // Token dado de baja en FCM: se limpia para no reintentar siempre.
                $usuario->forceFill(['fcm_token' => null])->save();
            } catch (Throwable $exception) {
                report($exception);
            }
        });
    }
}