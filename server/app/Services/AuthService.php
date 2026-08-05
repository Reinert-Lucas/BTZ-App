<?php
namespace App\Services;

use App\Models\Usuario;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class AuthService
{
    public function create(array $usuarioValidado)
    {
        return Usuario::create($usuarioValidado);
    }
    public function login(array $credentials): array
    {
        if (!Auth::attempt($credentials)) {
            throw new AuthenticationException('Credenciales incorrectas');
        }
        /** @var Usuario $user */
        $user = Auth::user();
        $user->tokens()->delete(); // Borra tokens previos si es que hay (Evita acumular tokens)
        $token = $user->createToken('API TOKEN', expiresAt: now()->addDays(7));
        return [
            'user' => $user,
            'token' => $token,
        ];
    }
    public function me(Request $request)
    {
        return $request->user();
    }
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();
    }
}