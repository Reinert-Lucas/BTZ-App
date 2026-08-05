<?php
namespace App\Services;

use App\Http\Requests\CreateUserRequest;
use App\Models\Usuario;

class UsuarioService
{
    public function index()
    {
        return Usuario::all();
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