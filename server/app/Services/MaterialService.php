<?php
namespace App\Services;

use App\Models\Material;

class MaterialService
{
    public function index()
    {
        return Material::paginate(10);
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