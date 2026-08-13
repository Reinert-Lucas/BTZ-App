<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Models\Usuario;
use App\Services\UsuarioService;
use Illuminate\Http\Request;

class WebUsuarioController extends Controller
{
    private UsuarioService $service;
    public function __construct(UsuarioService $service)
    {
        $this->service = $service;
    }
    public function index(Request $request)
    {
        $usuarios = $this->service->index($request);
        $columns = [
            [
                'label' => 'Nro',
                'field' => 'usuario_id'
            ],
            [
                'label' => 'Nombre',
                'field' => 'nombre',
            ],
            [
                'label' => 'DNI',
                'field' => 'dni',
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
            ],
            [
                'label' => 'Rol',
                'field' => 'rol',
            ],
        ];
        $filters = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ],
            [
                'label' => 'DNI',
                'field' => 'dni',
                'type' => 'text'
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
                'type' => 'tel'
            ],
            [
                'label' => 'Rol',
                'field' => 'rol',
                'type' => 'select',
                'options' => [
                    'admin' => 'admin',
                    'operario' => 'operario'
                ]
            ],
        ];
        return view('admin.usuarios.index', [
            'usuarios' => UsuarioResource::collection($usuarios),
            'columns' => $columns,
            'filtros' => $filters
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
                'label' => 'DNI',
                'field' => 'dni',
                'type' => 'text'
            ],
            [
                'label' => 'Contraseña',
                'field' => 'password',
                'type' => 'text'
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
                'type' => 'tel'
            ],
            [
                'label' => 'Rol',
                'field' => 'rol',
                'type' => 'select',
                'options' => [
                    'admin' => 'admin',
                    'operario' => 'operario'
                ]
            ],
        ];
        return view('admin.usuarios.create', ['inputs' => $inputs]);
    }
    public function store(CreateUserRequest $request)
    {
        $this->service->create($request->validated());
        return redirect()->route('admin.usuarios.index')->with([
            'status' => true,
            'message' => 'Usuario creado con exito'
        ]);
    }
    public function edit(int $usuario)
    {
        $inputs = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ],
            [
                'label' => 'DNI',
                'field' => 'dni',
                'type' => 'text'
            ],
            [
                'label' => 'Contraseña',
                'field' => '',
                'type' => 'text'
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
                'type' => 'tel'
            ],
            [
                'label' => 'Rol',
                'field' => 'rol',
                'type' => 'select',
                'options' => [
                    'admin' => 'admin',
                    'operario' => 'operario'
                ]
            ],
        ];
        $usuarioObtenido = $this->service->show($usuario);
        return view('admin.usuarios.edit', [
            'usuario' => new UsuarioResource($usuarioObtenido)->additional([
                'status' => true,
                'message' => 'Usuarios obtenidos con exito'
            ]),
            'inputs' => $inputs
        ]);
    }
    public function update(CreateUserRequest $request, Usuario $usuario)
    {
        $this->service->update($request->validated(), $usuario);
        return redirect()->route('admin.usuarios.index')->with([
            'status' => true,
            'message' => 'Usuario actualizado con exito'
        ]);
    }
    public function destroy(int $usuario)
    {
        $this->service->delete($usuario);
        return redirect()->route('admin.usuarios.index')->with([
            'status' => true,
            'message' => 'Usuario eliminado con exito'
        ]);
    }
}
