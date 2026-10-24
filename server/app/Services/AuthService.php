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
        $usuario = Usuario::where('dni', $credentials['dni'])->first();

        // Se verifica SIEMPRE un hash: el tiempo de respuesta no revela si el DNI existe.
        $hash = $usuario?->password ?? Hash::make(Str::random(32));
        $passwordValida = Hash::check($credentials['password'], $hash);

        if (!$usuario || !$passwordValida) {
            throw new AuthenticationException('Credenciales incorrectas');
        }

        return DB::transaction(function () use ($usuario) {
            $usuario->tokens()->delete(); // un solo dispositivo activo por usuario
            $token = $usuario->createToken('API TOKEN', expiresAt: now()->addDays(7));

            return [
                'user' => $usuario,
                'token' => $token->plainTextToken,
            ];
        });
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