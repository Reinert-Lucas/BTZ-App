<?php

namespace App\Http\Controllers;

use App\Http\Requests\AvisoRequest;
use App\Http\Resources\AvisoResource;
use App\Models\Aviso;
use App\Services\AvisoService;
use Illuminate\Http\Request;

class AvisoController extends Controller
{
    private AvisoService $service;
    public function __construct(AvisoService $service)
    {
        $this->service = $service;
    }
    public function index(Request $request)
    {
        $avisos = $this->service->index($request);
        return AvisoResource::collection($avisos)->additional([
            'status' => true,
            'message' => 'Avisos obtenidos con exito'
        ]);
    }
    public function show(int $aviso)
    {
        $avisoObtenido = $this->service->show($aviso);
        return (new AvisoResource($avisoObtenido))->additional([
            'status' => true,
            'message' => 'Aviso obtenido con exito'
        ]);
    }

    public function store(AvisoRequest $request)
    {
        $aviso = $this->service->create($request->validated());
        return (new AvisoResource($aviso))->additional([
            'status' => true,
            'message' => 'Aviso creado con exito'
        ]);
    }
    public function update(AvisoRequest $request, Aviso $aviso)
    {
        $avisoActualizado = $this->service->update($request->validated(), $aviso);
        return (new AvisoResource($avisoActualizado))->additional([
            'status' => true,
            'message' => 'Aviso actualizado con exito'
        ]);
    }
    public function delete(int $aviso)
    {
        $this->service->delete($aviso);
        return response()->json([
            'status' => true,
            'message' => 'Aviso eliminado con exito'
        ], 200);
    }
}
