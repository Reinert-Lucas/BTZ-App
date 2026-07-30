<?php
namespace App\Services;

use App\Models\Cliente;

class ClienteService
{
    public function index()
    {
        return Cliente::all();
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
        return Cliente::find($cliente)->deleteOrFail();
    }
}