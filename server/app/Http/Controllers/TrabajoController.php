<?php

namespace App\Http\Controllers;

use App\Http\Requests\TrabajoRequest;
use App\Http\Resources\AvisoResource;
use App\Http\Resources\TrabajoResource;
use App\Models\Aviso;
use App\Models\Trabajo;
use Illuminate\Support\Facades\Auth;

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
        return (new TrabajoResource($trabajo))->additional([
            'status' => true,
            'message' => 'Trabajo cargado correctamente',
        ]);
    }
    public function indexFinalizado(?int $usuario_id = null)
    {
        if ($usuario_id) {
            $avisos = Aviso::where([
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
        $avisos = Aviso::where([
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
