<?php

namespace App\Http\Controllers;

use App\Models\Cliente;
use App\Http\Requests\ClienteRequest;
use App\Http\Resources\ClienteResource;
use App\Services\ClienteService;

class ClienteController extends Controller
{
    private ClienteService $service;
    public function __construct(ClienteService $service)
    {
        $this->service = $service;
    }
    public function index()
    {
        $clientes = $this->service->index();
        return ClienteResource::collection($clientes)->additional([
            'status' => true,
            'message' => 'Clientes obtenidos con exito'
        ]);
    }
    public function show(int $cliente)
    {
        $clienteObtenido = $this->service->show($cliente);
        return (new ClienteResource($clienteObtenido))->additional([
            'status' => true,
            'message' => 'Cliente obtenido con exito'
        ]);
    }

    public function store(ClienteRequest $request)
    {
        $cliente = $this->service->create($request->validated());
        return (new ClienteResource($cliente))->additional([
            'status' => true,
            'message' => 'Cliente creado con exito'
        ]);
    }
    public function update(ClienteRequest $request, Cliente $cliente)
    {
        $clienteActualizado = $this->service->update($request->validated(), $cliente);
        return (new ClienteResource($clienteActualizado))->additional([
            'status' => true,
            'message' => 'Cliente actualizado con exito'
        ]);
    }
    public function delete(int $cliente)
    {
        $this->service->delete($cliente);
        return response()->json([
            'status' => true,
            'message' => 'Cliente eliminado con exito'
        ], 200);
    }
}
