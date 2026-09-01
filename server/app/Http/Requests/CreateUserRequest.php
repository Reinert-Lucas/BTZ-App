<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class CreateUserRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        /** @var \App\Models\Usuario|null $usuario */
        $usuario = $this->route('usuario');
        return [
            'nombre' => ['required', 'string', 'max:60'],
            'password' => ['string'],
            'rol' => ['required', Rule::in(['admin', 'operario'])],
            'dni' => [
                'required',
                'string',
                'max:8',
                Rule::unique('usuarios', 'dni')->ignore($usuario?->usuario_id, 'usuario_id'),
            ],
            'telefono' => ['required', 'string', 'max:25'],
            'activo' => ['sometimes', 'boolean'],
        ];

    }
}
