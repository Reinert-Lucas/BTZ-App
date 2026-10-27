<?php

namespace App\Services;

use App\Models\Usuario;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;
use Laravel\Sanctum\PersonalAccessToken;

class AuthService
{
    public function login(array $credentials): array
    {
        $usuario = Usuario::where('dni', $credentials['dni'])->first();
        // Siempre se ejecuta UN solo Hash::check (exista o no el DNI): el tiempo de
        // respuesta no revela si el DNI existe. 
        $hash = $usuario?->password ?? $this->hashSenuelo();
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
    public function logout(Request $request): void
    {
        $usuario = $request->user();
        // Evita que el dispositivo siga recibiendo notificaciones del usuario anterior.
        $usuario->forceFill(['fcm_token' => null])->save();
        $token = $usuario->currentAccessToken();
        if ($token instanceof PersonalAccessToken) {
            $token->delete();
        }
    }
    /** Hash con el mismo costo configurado que los hashes reales (bcrypt rounds). */
    private function hashSenuelo(): string
    {
        return Cache::rememberForever(
            'auth.hash_senuelo',
            fn() => Hash::make(Str::random(32))
        );
    }
}