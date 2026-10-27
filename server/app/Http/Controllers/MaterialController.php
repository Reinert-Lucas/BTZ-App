<?php

namespace App\Http\Controllers;

use App\Http\Requests\MaterialRequest;
use App\Http\Resources\MaterialResource;
use App\Models\Material;
use App\Services\MaterialService;
use Illuminate\Http\Request;

class MaterialController extends Controller
{
    public function __construct(private readonly MaterialService $service)
    {
    }
    public function index(Request $request)
    {
        $materiales = $this->service->index($request);
        return MaterialResource::collection($materiales)->additional([
            'status' => true,
            'message' => 'Materiales obtenidos con exito'
        ]);
    }
    public function show(int $material)
    {
        $materialObtenido = $this->service->show($material);
        return (new MaterialResource($materialObtenido))->additional([
            'status' => true,
            'message' => 'Material obtenido con exito'
        ]);
    }
    public function store(MaterialRequest $request)
    {
        $material = $this->service->create($request->validated());
        return (new MaterialResource($material))->additional([
            'status' => true,
            'message' => 'Material creado con exito'
        ]);
    }
    public function update(MaterialRequest $request, Material $material)
    {
        $materialActualizado = $this->service->update($request->validated(), $material);
        return (new MaterialResource($materialActualizado))->additional([
            'status' => true,
            'message' => 'Material actualizado con exito'
        ]);
    }
    public function destroy(int $material)
    {
        $this->service->destroy($material);
        return response()->json([
            'status' => true,
            'message' => 'Material eliminado con exito'
        ], 200);
    }
}
