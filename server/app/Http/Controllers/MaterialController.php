<?php

namespace App\Http\Controllers;

use App\Http\Requests\MaterialRequest;
use App\Http\Resources\MaterialResource;
use App\Models\Material;
use Illuminate\Http\Request;

class MaterialController extends Controller
{
    public function index()
    {
        $materiales = Material::all();
        return MaterialResource::collection($materiales)->additional([
            'status' => true,
            'message' => 'Materiales obtenidos con exito'
        ]);
    }
    public function show(Material $material)
    {
        return (new MaterialResource($material))->additional([
            'status' => true,
            'message' => 'Material obtenido con exito'
        ]);
    }

    public function store(MaterialRequest $request)
    {
        $material = Material::create($request->all());
        return (new MaterialResource($material))->additional([
            'status' => true,
            'message' => 'Material creado con exito'
        ]);
    }
    public function update(MaterialRequest $request, Material $material)
    {
        $material->update($request->all());
        return (new MaterialResource($material))->additional([
            'status' => true,
            'message' => 'Material actualizado con exito'
        ]);
    }
    public function delete(Material $material)
    {
        $material->delete();
        return response()->json([
            'status' => true,
            'message' => 'Material eliminado con exito'
        ], 200);
    }
}
