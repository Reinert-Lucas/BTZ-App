<?php

namespace App\Http\Controllers;

use App\Http\Requests\CreateUserRequest;
use App\Http\Requests\LoginUserRequest;
use App\Http\Resources\UsuarioResource;
use App\Services\AuthService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class WebAuthController extends Controller
{
    private AuthService $service;
    public function __construct(AuthService $service)
    {
        $this->service = $service;
    }
    public function create(CreateUserRequest $request)
    {
        $usuario = $this->service->create($request->validated());
        return view('admin.dashboard', [
            'status' => true,
            'message' => 'usuario registrado con exito',
            'token' => $usuario->createToken('API TOKEN')->plainTextToken
        ]);
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
