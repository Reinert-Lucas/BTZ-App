<?php

namespace App\Services;

use App\Models\Usuario;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

class UsuarioService
{
    public function index(Request $request)
    {
        return Usuario::query()
            ->when($request->usuario_id, function ($query, $usuario_id) {
                $query->where('usuario_id', $usuario_id);
            })
            ->when($request->nombre, function ($query, $nombre) {
                $query->where('nombre', 'like', "%{$nombre}%");
            })
            ->when($request->dni, function ($query, $dni) {
                $query->where('dni', $dni);
            })
            ->when($request->rol, function ($query, $rol) {
                $query->where('rol', $rol);
            })
            ->when($request->eliminados, function ($query) {
                $query->onlyTrashed();
            })
            ->orderBy('usuario_id', 'desc')
            ->paginate(10)
            ->withQueryString();
    }
    public function show(int $usuario)
    {
        return Usuario::findOrFail($usuario);
    }
    public function create(array $usuarioValidado)
    {
        return Usuario::create($usuarioValidado);
    }
    public function update(array $newUsuario, Usuario $usuario)
    {
        $esUnoMismo = (int) $usuario->usuario_id === (int) Auth::id();
        $nuevoRol = $newUsuario['rol'] ?? $usuario->rol;
        // El admin que opera siempre sigue siendo admin: nunca quedan 0 administradores.
        abort_if($esUnoMismo && $nuevoRol !== 'admin', 409, 'No puedes quitarte el rol de administrador.');
        if (empty($newUsuario['password'])) {
            unset($newUsuario['password']);
        }
        return DB::transaction(function () use ($usuario, $newUsuario) {
            $usuario->update($newUsuario);
            // Contraseña nueva => se invalidan los tokens de API existentes.
            if ($usuario->wasChanged('password')) {
                $usuario->tokens()->delete();
            }
            return $usuario->fresh();
        });
    }
    public function destroy(int $usuario): bool
    {
        abort_if($usuario === (int) Auth::id(), 409, 'No puedes eliminar tu propio usuario.');
        return DB::transaction(function () use ($usuario) {
            $usuarioObtenido = Usuario::lockForUpdate()->findOrFail($usuario);
            // Un aviso pendiente sin operario activo queda huérfano: primero hay que reasignarlo.
            abort_if(
                $usuarioObtenido->aviso()->where('estado', 'pendiente')->exists(),
                409,
                'El usuario tiene avisos pendientes. Reasígnalos o eliminalos antes.'
            );
            // Que el dispositivo no siga autenticado ni recibiendo notificaciones.
            $usuarioObtenido->tokens()->delete();
            $usuarioObtenido->forceFill(['fcm_token' => null])->save();
            return (bool) $usuarioObtenido->delete();
        });
    }
    public function restore(int $usuario): bool
    {
        return (bool) Usuario::withTrashed()->findOrFail($usuario)->restore();
    }
    public function forceDelete(int $usuario): bool
    {
        return DB::transaction(function () use ($usuario) {
            $usuarioObtenido = Usuario::onlyTrashed()->lockForUpdate()->findOrFail($usuario);
            abort_if(
                $usuarioObtenido->aviso()->withTrashed()->exists(),
                409,
                'No se puede eliminar para siempre un usuario con avisos relacionados.'
            );
            $usuarioObtenido->tokens()->delete();
            $usuarioObtenido->forceFill(['fcm_token' => null])->save();
            return (bool) $usuarioObtenido->forceDelete();
        });
    }
    /** Un token FCM pertenece a un único usuario: se lo quita a cualquier otro. */
    public function saveFcmToken(Usuario $usuario, string $token): void
    {
        DB::transaction(function () use ($usuario, $token) {
            Usuario::withTrashed()
                ->where('fcm_token', $token)
                ->where('usuario_id', '!=', $usuario->usuario_id)
                ->update(['fcm_token' => null]);
            $usuario->forceFill(['fcm_token' => $token])->save();
        });
    }
}
