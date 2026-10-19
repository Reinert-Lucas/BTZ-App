<?php

namespace App\Services;

use App\Http\Resources\AvisoResource;
use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Usuario;
use Illuminate\Http\Request;
use Throwable;

class AvisoService
{
    public function __construct(
        protected FirebaseNotificationService $firebaseNotificationService
    ) {
    }
    public function getFields(bool $isUpdate)
    {
        $inputs = [
            [
                'label' => 'Fecha',
                'field' => 'fecha',
                'type' => 'date'
            ],
            [
                'label' => 'Hora',
                'field' => 'hora',
                'type' => 'time'
            ],
            [
                'label' => 'Direccion',
                'field' => 'direccion',
                'type' => 'text'
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
                'type' => 'tel'
            ],
            [
                'label' => 'Mensaje',
                'field' => 'mensaje',
                'type' => 'textarea'
            ],
            [
                'label' => 'Observacion',
                'field' => 'observacion',
                'type' => 'textarea'
            ],
            [
                'label' => 'Urgencia',
                'field' => 'urgencia',
                'type' => 'select',
                'options' => [
                    'urgente' => 'Urgente',
                    'media' => 'Media',
                    'baja' => 'Baja'
                ]
            ],
            [
                'label' => 'Cliente',
                'field' => 'cliente_id',
                'type' => 'select',
                'options' => Cliente::pluck('nombre', 'cliente_id')
            ],
            [
                'label' => 'Operario',
                'field' => 'usuario_id',
                'type' => 'select',
                'options' => Usuario::where('rol', 'operario')->pluck('nombre', 'usuario_id')
            ]
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
                ]
            ];
        }

        return $inputs;
    }

    

    
public function index(Request $request)
{
    $usuarioAutenticado = auth()->user();

    $avisos = Aviso::query()
        ->with(['usuario', 'cliente', 'trabajo.materiales']);

    if ($usuarioAutenticado && $usuarioAutenticado->rol === 'operario') {
        $avisos->where('usuario_id', $usuarioAutenticado->usuario_id);
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
        });

    return $this->ordenarAvisos($avisos)
        ->paginate(10)
        ->withQueryString();
}

    

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

        $usuario = $aviso->usuario;

        if ($usuario && $usuario->fcm_token) {
            try {
                $this->firebaseNotificationService->sendToToken(
                    $usuario->fcm_token,
                    '✅ Nuevo Aviso Asignado',
                    'Se te ha asignado un nuevo aviso. Toca para ver los detalles.',
                    [
                        'aviso_id' => (string) $aviso->aviso_id,
                    ]
                );
            } catch (Throwable $exception) {
                report($exception);
            }
        }

        return $aviso;
    }

    public function update(array $newAviso, Aviso $aviso)
    {
        $aviso->update($newAviso);
        return $aviso->fresh();
    }

    public function destroy(int $aviso)
    {
        return Aviso::findOrFail($aviso)->delete();
    }
}