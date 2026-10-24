<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateUserRequest;
use App\Http\Requests\LoginUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Services\AuthService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

/* 
    Controlador de autenticacion, maneja el registro, login, logout y obtener los datos del usuario autenticado.
    No agregar logica de negocio relacionada a los usuarios aqui, solo lo relacionado a la autenticacion.
*/
/* 
    Auth guarda la informacion del usuario autenticado en el contexto de la aplicacion
    Se puede acceder a ella usando Auth::user() o $request->user() 
    SOLO en rutas protegidas por el middleware auth:sanctum.
*/

class WebAuthController extends Controller
{
    public function __construct(private readonly AuthService $service)
    {
    }
    public function login(LoginUserRequest $request)
    {
        if (!Auth::attempt($request->validated())) {
            return back()
                ->withErrors([
                    'dni' => 'Credenciales incorrectas.',
                ])
                ->onlyInput('dni');
        }
        $request->session()->regenerate();
        return redirect()->route('admin.dashboard');
    }
    public function me(Request $request)
    {
        $usuario = $this->service->me($request);
        return response()->json([
            'status' => true,
            'message' => 'datos del usuario autenticado',
            'user' => new UsuarioResource($usuario),
        ], 200);
    }
    public function logout(Request $request)
    {
        Auth::logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();
        return redirect()->route('login');
    }
}
