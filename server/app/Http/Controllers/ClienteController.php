<?php

namespace App\Http\Controllers;

use App\Models\Cliente;
use App\Http\Requests\ClienteRequest;
use App\Http\Resources\ClienteResource;

class ClienteController extends Controller
{
    public function index()
    {
        $clientes = Cliente::all();
        return ClienteResource::collection($clientes)->additional([
            'status' => true,
            'message' => 'Clientes obtenidos con exito'
        ]);
    }
    public function show(Cliente $cliente)
    {
        if (!$cliente->activo) {
            return response()->json([
                'status' => false,
                'message' => 'Cliente no encontrado'
            ], 404);
        }
        return (new ClienteResource($cliente))->additional([
            'status' => true,
            'message' => 'Cliente obtenido con exito'
        ]);
    }

    public function store(ClienteRequest $request)
    {
        $cliente = Cliente::create($request->validated());
        return (new ClienteResource($cliente))->additional([
            'status' => true,
            'message' => 'Cliente creado con exito'
        ]);
    }
    public function update(ClienteRequest $request, Cliente $cliente)
    {
        $cliente->update($request->validated());
        return (new ClienteResource($cliente))->additional([
            'status' => true,
            'message' => 'Cliente actualizado con exito'
        ]);
    }
    public function delete(Cliente $cliente)
    {
        $cliente->delete();
        return response()->json([
            'status' => true,
            'message' => 'Cliente eliminado con exito'
        ], 200);
    }
}
