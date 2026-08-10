<?php
namespace App\Services;

use App\Http\Requests\CreateUserRequest;
use App\Models\Usuario;
use Illuminate\Http\Request;

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
        $usuario->update($newUsuario);
        return $usuario->fresh();
    }
    public function delete(int $usuario)
    {
        return Usuario::findOrFail($usuario)->delete();
    }
}