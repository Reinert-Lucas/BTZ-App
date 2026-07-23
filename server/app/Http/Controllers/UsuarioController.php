<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Models\Usuario;
use Illuminate\Database\Eloquent\Collection;
use Illuminate\Http\Request;
class UsuarioController extends Controller
{
    public function index()
    {
        $usuarios = Usuario::all()->where('activo', true);
        return UsuarioResource::collection($usuarios)->additional([
            'status' => true,
            'message' => 'Usuarios obtenidos con exito'
        ]);
    }
    public function show(Usuario $usuario)
    {
        if (!$usuario->activo) {
            return response()->json([
                'status' => false,
                'message' => 'Usuario no encontrado'
            ], 404);
        }
        return (new UsuarioResource($usuario))->additional([
            'status' => true,
            'message' => 'Usuario obtenido con exito'
        ]);
    }

    public function store(CreateUserRequest $request)
    {
        $usuario = Usuario::create($request->all());
        return (new UsuarioResource($usuario))->additional([
            'status' => true,
            'message' => 'Usuario creado con exito'
        ]);
    }
    public function update(CreateUserRequest $request, Usuario $usuario)
    {
        $usuario->update($request->all());
        return (new UsuarioResource($usuario))->additional([
            'status' => true,
            'message' => 'Usuario actualizado con exito'
        ]);
    }
    public function delete(Usuario $usuario)
    {
        $usuario->update(['activo' => false]);
        return response()->json([
            'status' => true,
            'message' => 'Usuario eliminado con exito'
        ], 200);
    }
}
