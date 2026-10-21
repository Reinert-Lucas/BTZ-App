<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class TrabajoRequest extends FormRequest
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
        return [
            'trabajo_realizado' => ['required', 'string', 'max:255'],
            'desperfecto' => ['required', 'string', 'max:255'],
            'aviso_id' => [
                'required',
                'integer',
                Rule::exists('avisos', 'aviso_id')->whereNull('deleted_at'),
                Rule::unique('trabajos', 'aviso_id'),
            ],
            'materiales' => ['required', 'array', 'min:1'],
            'materiales.*.material_id' => [
                'required',
                'integer',
                'distinct',
                Rule::exists('materiales', 'material_id')->whereNull('deleted_at'),
            ],
            'materiales.*.cantidad' => ['required', 'integer', 'min:1', 'max:9999'],
        ];
    }
}