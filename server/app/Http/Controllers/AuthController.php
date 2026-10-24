<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Http\Requests\CreateUserRequest;
use App\Http\Requests\LoginUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Services\AuthService;
use Illuminate\Auth\AuthenticationException;

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
    public function __construct(private readonly AuthService $service)
    {
    }

    public function login(LoginUserRequest $request)
    {
        try {
            $data = $this->service->login($request->validated());
        } catch (AuthenticationException) {
            return response()->json([
                'status' => false,
                'message' => 'Credenciales incorrectas',
            ], 401);
        }

        return response()->json([
            'status' => true,
            'message' => 'Sesión iniciada con éxito',
            'user' => new UsuarioResource($data['user']),
            'token' => $data['token'], // El token pasa de ser NewAccessToken a string [REVISAR SI ANDA]
        ]);
    }

    public function me(Request $request)
    {
        return response()->json([
            'status' => true,
            'message' => 'datos del usuario autenticado',
            'user' => new UsuarioResource($this->service->me($request)),
        ]);
    }

    public function logout(Request $request)
    {
        $this->service->logout($request);

        return response()->json([
            'status' => true,
            'message' => 'sesion cerrada con exito',
        ]);
    }
}
