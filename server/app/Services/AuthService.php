<?php
namespace App\Services;

use App\Models\Usuario;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;

class AuthService
{
    public function login(array $credentials): array
    {
        $usuario = Usuario::where('dni', $credentials['dni'])->first();

        if (!$usuario || !Hash::check($credentials['password'], $usuario->password)) {
            throw new AuthenticationException('Credenciales incorrectas');
        }

        $usuario->tokens()->delete(); // un solo dispositivo activo por usuario
        $token = $usuario->createToken('API TOKEN', expiresAt: now()->addDays(7));

        return [
            'user' => $usuario,
            'token' => $token->plainTextToken,
        ];
    }

    public function me(Request $request)
    {
        return $request->user();
    }

    public function logout(Request $request): void
    {
        $usuario = $request->user();
        // Evita que el dispositivo siga recibiendo notificaciones del usuario anterior.
        $usuario->forceFill(['fcm_token' => null])->save();
        $usuario->currentAccessToken()->delete();
    }
}