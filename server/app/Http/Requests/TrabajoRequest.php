<?php

namespace App\Http\Requests;

use App\Models\Aviso;
use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class TrabajoRequest extends FormRequest
{
    /**
     * Solo el operario asignado puede cargar el trabajo. Si el aviso no existe o es de otro
     * operario la respuesta es la misma (403): no se revela qué IDs existen.
     * El controller vuelve a verificar dentro del lock (fuente de verdad).
     */
    public function authorize(): bool
    {
        $aviso = Aviso::find($this->integer('aviso_id'));

        return $aviso !== null
            && (int) $aviso->usuario_id === (int) $this->user()?->usuario_id;
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
