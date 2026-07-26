<?php

namespace App\Http\Controllers;

use App\Http\Requests\AvisoRequest;
use App\Http\Resources\AvisoResource;
use App\Models\Aviso;
class AvisoController extends Controller
{
    public function index()
    {
        $avisos = Aviso::with(['usuario', 'cliente'])->where('estado', 'pendiente')->get();
        return AvisoResource::collection($avisos)->additional([
            'status' => true,
            'message' => 'Avisos obtenidos con exito'
        ]);
    }
    public function show(Aviso $aviso)
    {
        return (new AvisoResource($aviso))->additional([
            'status' => true,
            'message' => 'Aviso obtenido con exito'
        ]);
    }

    public function store(AvisoRequest $request)
    {
        $aviso = Aviso::create($request->validated());
        return (new AvisoResource($aviso))->additional([
            'status' => true,
            'message' => 'Aviso creado con exito'
        ]);
    }
    public function update(AvisoRequest $request, Aviso $aviso)
    {
        $aviso->update($request->validated());
        return (new AvisoResource($aviso))->additional([
            'status' => true,
            'message' => 'Aviso actualizado con exito'
        ]);
    }
    public function delete(Aviso $aviso)
    {
        $aviso->update(['estado' => 'cancelado']);
        return response()->json([
            'status' => true,
            'message' => 'Aviso eliminado con exito'
        ], 200);
    }
}
