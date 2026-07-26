<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Http\Requests\CreateUserRequest;
use App\Http\Requests\LoginUserRequest;
use App\Models\Usuario;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use App\Http\Resources\UsuarioResource;

/* 
    Controlador de autenticacion, maneja el registro, login, logout y obtener los datos del usuario autenticado.
    No agregar logica de negocio relacionada a los usuarios aqui, solo lo relacionado a la autenticacion.
*/
/* 
    Auth guarda la informacion del usuario autenticado en el contexto de la aplicacion
    Se puede acceder a ella usando Auth::user() o $request->user() 
    SOLO en rutas protegidas por el middleware auth:sanctum.
*/

class AuthController extends Controller
{
    public function create(CreateUserRequest $request)
    {
        $usuario = Usuario::create($request->validated());
        return response()->json([
            'status' => true,
            'message' => 'usuario registrado con exito',
            'token' => $usuario->createToken('API TOKEN')->plainTextToken
        ], 200);
    }
    public function login(LoginUserRequest $request)
    {
        if (!Auth::attempt(['dni' => $request->dni, 'password' => $request->password])) {
            return response()->json([
                'status' => false,
                'message' => 'credenciales erroneas'
            ], 401);
        }
        /** @var \App\Models\Usuario $user */
        $user = Auth::user();
        $token = $user->createToken('API TOKEN')->plainTextToken;

        return response()->json([
            'status' => true,
            'message' => 'sesion iniciada con exito',
            'user' => new UsuarioResource($user),
            'token' => $token,
        ], 200);
    }
    public function me(Request $request)
    {
        return response()->json([
            'status' => true,
            'message' => 'datos del usuario autenticado',
            'user' => new UsuarioResource($request->user()),
        ], 200);
    }
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();
        return response()->json([
            'status' => true,
            'message' => 'sesion cerrada con exito'
        ], 200);
    }
}
