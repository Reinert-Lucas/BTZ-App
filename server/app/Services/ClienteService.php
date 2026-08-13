<?php
namespace App\Services;

use App\Models\Cliente;
use Illuminate\Http\Request;

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
            ->when($request->asegurado, function ($query, $asegurado) {
                $query->where('asegurado', $asegurado);
            })
            ->when($request->asegurado_detalle, function ($query, $asegurado_detalle) {
                $query->where('asegurado', $asegurado_detalle);
            })
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
    public function delete(int $cliente)
    {
        return Cliente::findOrFail($cliente)->delete();
    }
}