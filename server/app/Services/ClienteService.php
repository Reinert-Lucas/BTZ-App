<?php

namespace App\Services;

use App\Models\Cliente;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;

class ClienteService
{
    public function index(Request $request)
    {
        return Cliente::query()
            ->when($request->cliente_id, fn($q, $v) => $q->where('cliente_id', $v))
            ->when($request->nombre, fn($q, $v) => $q->where('nombre', 'like', "%{$v}%"))
            ->when($request->email, fn($q, $v) => $q->where('email', $v))
            ->when($request->filled('asegurado'), fn($q) => $q->where('asegurado', $request->boolean('asegurado')))
            ->when($request->asegurado_detalle, fn($q, $v) => $q->where('asegurado_detalle', 'like', "%{$v}%"))
            ->when($request->boolean('eliminados'), fn($q) => $q->onlyTrashed())
            ->orderBy('cliente_id', 'desc')
            ->paginate(10)
            ->withQueryString();
    }

    public function show(int $cliente)
    {
        return Cliente::findOrFail($cliente);
    }

    public function create(array $clienteValidado)
    {
        return Cliente::create($clienteValidado);
    }

    public function update(array $newCliente, Cliente $cliente)
    {
        $cliente->update($newCliente);
        return $cliente->fresh();
    }

    public function destroy(int $cliente): bool
    {
        return DB::transaction(function () use ($cliente) {
            $clienteObtenido = Cliente::lockForUpdate()->findOrFail($cliente);
            abort_if(
                $clienteObtenido->aviso()->where('estado', 'pendiente')->exists(),
                409,
                'El cliente tiene avisos pendientes. Cancélalos o cambia su cliente antes de eliminarlo.'
            );
            return (bool) $clienteObtenido->delete();
        });
    }

    public function restore(int $cliente): bool
    {
        return (bool) Cliente::onlyTrashed()->findOrFail($cliente)->restore();
    }

    public function forceDelete(int $cliente): bool
    {
        return DB::transaction(function () use ($cliente) {
            $clienteObtenido = Cliente::onlyTrashed()->lockForUpdate()->findOrFail($cliente);
            abort_if(
                $clienteObtenido->aviso()->withTrashed()->exists(),
                409,
                'No se puede eliminar para siempre un cliente con avisos relacionados.'
            );
            $borrado = (bool) $clienteObtenido->forceDelete();
            Log::warning('Eliminación permanente', [
                'modelo' => 'Cliente',
                'id' => $cliente,
                'usuario_id' => Auth::id(),
            ]);
            return $borrado;
        });
    }
}
