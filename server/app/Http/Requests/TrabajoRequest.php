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
            'aviso_id' => ['required', 'exists:avisos,aviso_id', 'unique:trabajos,aviso_id'],
            'materiales' => [
                'required',
                'array',
                function ($attribute, $value, $fail) {
                    $ids = array_column($value, 'material_id');
                    if (count($ids) !== count(array_unique($ids))) {
                        $fail('No se puede repetir el mismo material.');
                    }
                }
            ],
            'materiales.*.material_id' => ['required', 'exists:materiales,material_id'],
            'materiales.*.cantidad' => ['required', 'integer', 'min:1'],
        ];
    }
}
