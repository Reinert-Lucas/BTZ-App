<?php

namespace App\Services;

use App\Models\Material;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;

class MaterialService
{
    public function index(Request $request)
    {
        // Los operarios consumen este listado desde la app: la papelera es solo para admins.
        $verEliminados = $request->boolean('eliminados') && Auth::user()?->rol === 'admin';

        return Material::query()
            ->when($request->material_id, fn($q, $v) => $q->where('material_id', $v))
            ->when($request->nombre, fn($q, $v) => $q->where('nombre', 'like', "%{$v}%"))
            ->when($verEliminados, fn($q) => $q->onlyTrashed())
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
        return (bool) Material::onlyTrashed()->findOrFail($material)->restore();
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
            $borrado = (bool) $materialObtenido->forceDelete();
            Log::warning('Eliminación permanente', [
                'modelo' => 'Material',
                'id' => $material,
                'usuario_id' => Auth::id(),
            ]);
            return $borrado;
        });
    }
}
