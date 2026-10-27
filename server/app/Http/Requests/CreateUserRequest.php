<?php

namespace App\Http\Requests;

use App\Models\Usuario;
use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;
use Illuminate\Validation\Rules\Password;

class CreateUserRequest extends FormRequest
{
    public function authorize(): bool
    {
        return true;
    }

    /**
     * @return array<string, ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        /** @var Usuario|null $usuario */
        $usuario = $this->route('usuario');
        $esAlta = $usuario === null;

        return [
            'nombre' => ['required', 'string', 'max:60'],
            // Alta: obligatoria. Edición: opcional (vacía = no cambia la contraseña).
            'password' => [$esAlta ? 'required' : 'nullable', 'string', Password::min(8)],
            'rol' => ['required', Rule::in(['admin', 'operario'])],
            // Mismo criterio que el comando usuarios:crear-admin (DNI argentino: 7 u 8 dígitos).
            // El unique de BD también cuenta usuarios eliminados (soft delete).
            'dni' => [
                'required',
                'string',
                'digits_between:7,8',
                Rule::unique('usuarios', 'dni')->ignore($usuario?->usuario_id, 'usuario_id'),
            ],
            'telefono' => ['required', 'string', 'max:20', 'regex:/^[0-9+\-\s()]+$/'],
        ];
    }
}