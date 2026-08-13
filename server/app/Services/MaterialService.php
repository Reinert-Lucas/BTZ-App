<?php
namespace App\Services;

use App\Models\Material;
use Illuminate\Http\Request;

class MaterialService
{
    public function index(Request $request)
    {
        return Material::query()
            ->when($request->material_id, function ($query, $material_id) {
                $query->where('material_id', $material_id);
            })
            ->when($request->nombre, function ($query, $nombre) {
                $query->where('nombre', 'like', "%{$nombre}%");
            })
            ->paginate(10)
            ->withQueryString();
    }
    public function show(int $material)
    {
        return Material::findOrFail($material);
    }
    public function create(array $materialValidado)
    {
        return Material::create($materialValidado);
    }
    public function update(array $newMaterial, Material $material)
    {
        $material->update($newMaterial);
        return $material->fresh();
    }
    public function delete(int $material)
    {
        return Material::findOrFail($material)->delete();
    }
}