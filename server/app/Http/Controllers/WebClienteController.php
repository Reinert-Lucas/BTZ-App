<?php

namespace App\Http\Controllers;

use App\Http\Requests\ClienteRequest;
use App\Http\Resources\ClienteResource;
use App\Models\Cliente;
use App\Services\ClienteService;

class WebClienteController extends Controller
{
    private ClienteService $service;
    public function __construct(ClienteService $service)
    {
        $this->service = $service;
    }
    public function index()
    {
        $clientes = $this->service->index();
        $columns = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
            ],
            [
                'label' => 'Email',
                'field' => 'email',
            ],
            [
                'label' => 'Asegurado',
                'field' => 'asegurado',
            ],
            [
                'label' => 'Asegurado Detalle',
                'field' => 'asegurado_detalle',
            ]
        ];
        return view('admin.clientes.index', [
            'clientes' => ClienteResource::collection($clientes)->additional([
                'status' => true,
                'message' => 'Clientes obtenidos con exito'
            ]),
            'columns' => $columns
        ]);
    }
    public function show()
    {
        //
    }
    public function create()
    {
        $inputs = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ],
            [
                'label' => 'Email',
                'field' => 'email',
                'type' => 'email'
            ],
            [
                'label' => 'Asegurado',
                'field' => 'asegurado',
                'type' => 'checkbox'
            ],
            [
                'label' => 'Asegurado Detalle',
                'field' => 'asegurado_detalle',
                'type' => 'text'
            ]
        ];
        return view('admin.clientes.create', ['inputs' => $inputs]);
    }
    public function store(ClienteRequest $request)
    {
        $this->service->create($request->validated());
        return redirect()->route('admin.clientes.index')->with([
            'status' => true,
            'message' => 'Cliente creado con exito'
        ]);
    }
    public function edit(int $cliente)
    {
        $inputs = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ],
            [
                'label' => 'Email',
                'field' => 'email',
                'type' => 'email'
            ],
            [
                'label' => 'Asegurado',
                'field' => 'asegurado',
                'type' => 'checkbox'
            ],
            [
                'label' => 'Asegurado Detalle',
                'field' => 'asegurado_detalle',
                'type' => 'text'
            ]
        ];
        $clienteObtenido = $this->service->show($cliente);
        return view('admin.clientes.edit', [
            'cliente' => new ClienteResource($clienteObtenido)->additional([
                'status' => true,
                'message' => 'Clientes obtenidos con exito'
            ]),
            'inputs' => $inputs
        ]);
    }
    public function update(ClienteRequest $request, Cliente $cliente)
    {
        $this->service->update($request->validated(), $cliente);
        return redirect()->route('admin.clientes.index')->with([
            'status' => true,
            'message' => 'Cliente actualizado con exito'
        ]);
    }
    public function destroy(int $cliente)
    {
        $this->service->delete($cliente);
        return redirect()->route('admin.clientes.index')->with([
            'status' => true,
            'message' => 'Cliente eliminado con exito'
        ]);
    }
}
