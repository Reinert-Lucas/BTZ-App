<?php
namespace App\Services;

use App\Models\Cliente;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ClienteService
{
    public function index(Request $request)
    {
        return Cliente::query()
            ->when($request->cliente_id, function ($query, $cliente_id) {
                $query->where('cliente_id', $cliente_id);
            })
            ->when($request->nombre, function ($query, $nombre) {
                $query->where('nombre', 'like', "%{$nombre}%");
            })
            ->when($request->email, function ($query, $email) {
                $query->where('email', $email);
            })
            ->when($request->filled('asegurado'), function ($query) use ($request) {
                $query->where('asegurado', $request->boolean('asegurado'));
            })
            ->when($request->asegurado_detalle, function ($query, $detalle) {
                $query->where('asegurado_detalle', 'like', "%{$detalle}%");
            })
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

            // Mismo criterio que UsuarioService::destroy: no dejar avisos pendientes huérfanos.
            abort_if(
                $clienteObtenido->aviso()->where('estado', 'pendiente')->exists(),
                409,
                'El cliente tiene avisos pendientes. Cancélalos o cambia su cliente antes de eliminarlo.'
            );

            return (bool) $clienteObtenido->delete();
        });
    }
}