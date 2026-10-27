<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Models\Usuario;
use App\Services\UsuarioService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class UsuarioController extends Controller
{
    public function __construct(private readonly UsuarioService $service)
    {
    }
    public function index(Request $request)
    {
        $usuarios = $this->service->index($request);
        return UsuarioResource::collection($usuarios)->additional([
            'status' => true,
            'message' => 'Usuarios obtenidos con exito'
        ]);
    }
    public function show(int $usuario)
    {
        $usuarioObtenido = $this->service->show($usuario);
        return (new UsuarioResource($usuarioObtenido))->additional([
            'status' => true,
            'message' => 'Usuario obtenido con exito'
        ]);
    }

    public function store(CreateUserRequest $request)
    {
        $usuario = $this->service->create($request->validated());
        return (new UsuarioResource($usuario))->additional([
            'status' => true,
            'message' => 'Usuario creado con exito'
        ]);
    }
    public function update(CreateUserRequest $request, Usuario $usuario)
    {
        $usuarioActualizado = $this->service->update($request->validated(), $usuario);
        return (new UsuarioResource($usuarioActualizado))->additional([
            'status' => true,
            'message' => 'Usuario actualizado con exito'
        ]);
    }
    public function destroy(int $usuario)
    {
        $this->service->destroy($usuario);
        return response()->json([
            'status' => true,
            'message' => 'Usuario eliminado con exito'
        ], 200);
    }
    public function saveFcmToken(Request $request)
    {
        $datos = $request->validate([
            'token' => ['required', 'string', 'max:255'],
        ]);
        $this->service->saveFcmToken($request->user(), $datos['token']);
        return response()->json([
            'status' => true,
            'message' => 'Token FCM actualizado',
        ]);
    }
}
