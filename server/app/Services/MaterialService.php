<?php

namespace App\Services;

use App\Models\Material;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

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
            ->when($request->eliminados, function ($query) {
                $query->onlyTrashed();
            })
            ->orderBy('material_id', 'desc')
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
    public function destroy(int $material)
    {
        return Material::findOrFail($material)->delete();
    }
    public function restore(int $material): bool
    {
        return (bool) Material::withTrashed()->findOrFail($material)->restore();
    }
    public function forceDelete(int $material): bool
    {
        return DB::transaction(function () use ($material) {
            $materialObtenido = Material::onlyTrashed()->lockForUpdate()->findOrFail($material);
            abort_if(
                DB::table('materiales_trabajos')->where('material_id', $material)->exists(),
                409,
                'No se puede eliminar para siempre un material usado en trabajos.'
            );
            return (bool) $materialObtenido->forceDelete();
        });
    }
}
