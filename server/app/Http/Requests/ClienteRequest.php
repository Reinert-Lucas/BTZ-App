<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class ClienteRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    protected function prepareForValidation(): void
    {
        $asegurado = $this->boolean('asegurado');

        $this->merge([
            'asegurado' => $asegurado,
            'asegurado_detalle' => $asegurado ? $this->input('asegurado_detalle') : null,
        ]);
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        return [
            'nombre' => ['required', 'string', 'max:50'],
            'email' => ['required', 'email', 'max:50'],
            'asegurado' => ['boolean'],
            'asegurado_detalle' => [
                Rule::requiredIf(fn() => $this->boolean('asegurado')),
                'nullable',
                'string',
                'max:255',
            ],
        ];
    }
}
