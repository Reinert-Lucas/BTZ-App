<?php

namespace App\Http\Requests;

use App\Models\Material;
use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class MaterialRequest extends FormRequest
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
        /** @var Material|null $material */
        $material = $this->route('material');

        return [
            'nombre' => [
                'required',
                'string',
                'max:50',
                Rule::unique('materiales', 'nombre')->ignore($material?->material_id, 'material_id'),
            ],
            'detalle' => ['nullable', 'string', 'max:100'], // la columna es nullable
        ];
    }
}