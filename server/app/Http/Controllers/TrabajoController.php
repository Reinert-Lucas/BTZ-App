<?php

namespace App\Http\Controllers;

use App\Http\Requests\TrabajoRequest;
use App\Http\Resources\AvisoResource;
use App\Http\Resources\TrabajoResource;
use App\Models\Aviso;
use App\Models\Trabajo;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

class TrabajoController extends Controller
{
    public function index()
    {
        $user = Auth::user();
        $trabajos = Aviso::where('usuario_id', $user->usuario_id)->get();
        return AvisoResource::collection($trabajos)->additional([
            'status' => true,
            'message' => 'Trabajos asignados al operario'
        ]);
    }
    public function store(TrabajoRequest $request)
    {
        // Verificar que el operario sea el asignado al Aviso
        if (Aviso::findOrFail($request->aviso_id)->usuario_id !== Auth::id()) {
            return response()->json([
                'status' => false,
                'message' => 'No tiene permisos para cargar el trabajo de este aviso',
            ], 403);
        }
        // Transaction evita datos "huerfanos" si falla alguna parte del create() o attach()
        $trabajo = DB::transaction(function () use ($request) {
            // Cargar datos del trabajo realizado
            $trabajo = Trabajo::create([
                'trabajo_realizado' => $request->trabajo_realizado,
                'desperfecto' => $request->desperfecto,
                'aviso_id' => $request->aviso_id,
            ]);
            // Cargar materiales utilizados
            foreach ($request->materiales as $material) {
                $trabajo->materiales()->attach(
                    $material['material_id'],
                    ['cantidad' => $material['cantidad']]
                );
            }
            // Marcar aviso como ['finalizado']
            $trabajo->aviso()->update([
                'estado' => 'finalizado'
            ]);
            // Cargar la relación
            $trabajo->load('materiales');
            return $trabajo;
        });
        return (new TrabajoResource($trabajo))->additional([
            'status' => true,
            'message' => 'Trabajo cargado correctamente',
        ]);
    }
    public function indexFinalizado(?int $usuario_id = null)
    {
        if ($usuario_id) {
            $avisos = Aviso::with(['usuario', 'cliente'])->where([
                'usuario_id' => $usuario_id,
                'estado' => 'finalizado'
            ])->get();
            // Cargar el trabajo hecho + materiales usados
            $avisos->load('trabajo.materiales');
            return AvisoResource::collection($avisos)->additional([
                'status' => true,
                'message' => 'Avisos terminados cargados correctamente',
            ]);
        }
        $avisos = Aviso::with(['usuario', 'cliente'])->where([
            'estado' => 'finalizado'
        ])->get();
        // Cargar el trabajo hecho + materiales usados
        $avisos->load('trabajo.materiales');
        return AvisoResource::collection($avisos)->additional([
            'status' => true,
            'message' => 'Avisos terminados cargados correctamente',
        ]);
    }
}
