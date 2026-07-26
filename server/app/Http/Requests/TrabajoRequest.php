<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;

class TrabajoRequest extends FormRequest
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
        return [
            'trabajo_realizado' => ['required', 'max:255'],
            'desperfecto' => ['required', 'max:255'],
            'aviso_id' => ['required'],
            'materiales' => ['required', 'array'],
            'materiales.*.material_id' => ['required', 'exists:materiales,material_id'],
            'materiales.*.cantidad' => ['required', 'integer', 'min:1'],
        ];
    }
}
