<?php

namespace App\Http\Controllers;

use App\Http\Requests\TrabajoRequest;
use App\Http\Resources\AvisoResource;
use App\Http\Resources\TrabajoResource;
use App\Models\Aviso;
use App\Models\Trabajo;
use App\Services\TrabajoService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TrabajoController extends Controller
{
    public function __construct(private readonly TrabajoService $trabajoService)
    {
    }

    public function index(Request $request)
    {
        $trabajos = $this->trabajoService->trabajosAsignados($request->user()->usuario_id);

        return AvisoResource::collection($trabajos)->additional([
            'status' => true,
            'message' => 'Trabajos asignados al operario',
        ]);
    }

    public function store(TrabajoRequest $request)
    {
        $usuario = $request->user();
        $datos = $request->validated();

        $trabajo = DB::transaction(function () use ($datos, $usuario) {
            // lockForUpdate: dos peticiones simultáneas no pueden finalizar el mismo aviso.
            $aviso = Aviso::whereKey($datos['aviso_id'])->lockForUpdate()->firstOrFail();

            abort_if(
                (int) $aviso->usuario_id !== (int) $usuario->usuario_id,
                403,
                'No tiene permisos para cargar el trabajo de este aviso'
            );
            abort_if($aviso->estado !== 'pendiente', 409, 'Este aviso ya fue resuelto o cancelado');

            $trabajo = Trabajo::create([
                'trabajo_realizado' => $datos['trabajo_realizado'],
                'desperfecto' => $datos['desperfecto'],
                'aviso_id' => $aviso->aviso_id,
            ]);

            $trabajo->materiales()->attach(
                collect($datos['materiales'])
                    ->mapWithKeys(fn($m) => [$m['material_id'] => ['cantidad' => $m['cantidad']]])
                    ->all()
            );

            $aviso->update(['estado' => 'finalizado']);

            return $trabajo->load('materiales');
        });

        return (new TrabajoResource($trabajo))
            ->additional(['status' => true, 'message' => 'Trabajo cargado correctamente'])
            ->response()
            ->setStatusCode(201);
    }

    public function indexFinalizado(Request $request, ?int $usuario_id = null)
    {
        $usuario = $request->user();
        if ($usuario->rol !== 'admin') {
            $usuario_id = $usuario->usuario_id;
        }

        return AvisoResource::collection($this->trabajoService->trabajosFinalizados($usuario_id))
            ->additional([
                'status' => true,
                'message' => 'Avisos terminados cargados correctamente',
            ]);
    }
}