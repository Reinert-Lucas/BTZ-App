<?php

namespace App\Http\Controllers;

use App\Models\Usuario;
use App\Http\Requests\LoginUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Services\AuthService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

/*
    Controlador de autenticacion web (sesion), maneja login, logout y datos del usuario autenticado.
    No agregar logica de negocio relacionada a los usuarios aqui, solo lo relacionado a la autenticacion.
*/

class WebAuthController extends Controller
{
    public function __construct(private readonly AuthService $service) {}

    public function showLogin()
    {
        return Auth::check()
            ? redirect()->route('admin.dashboard')
            : view('index');
    }

    public function login(LoginUserRequest $request)
    {
        $credentials = $request->validated();
        $usuario = Usuario::where('dni', $credentials['dni'])->first();

        if (!$usuario || $usuario->password !== $credentials['password']) {
            return back()
                ->withErrors([
                    'dni' => 'Credenciales incorrectas.',
                ])
                ->onlyInput('dni');
        }

        Auth::login($usuario);
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
