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
        $token = $user->createToken('API TOKEN')->plainTextToken;
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