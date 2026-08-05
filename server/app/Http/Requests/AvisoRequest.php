<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class AvisoRequest extends FormRequest
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
            'fecha' => ['required', 'date'],
            'hora' => ['required'],
            'direccion' => ['required', 'max:50'],
            'telefono' => ['required', 'max:20'],
            'mensaje' => ['max:255'],
            'observacion' => ['max:255'],
            'estado' => ['required', Rule::in(['pendiente', 'finalizado', 'cancelado'])],
            'urgencia' => ['required', Rule::in(['urgente', 'media', 'baja'])],
            'usuario_id' => ['required', 'exists:usuarios,usuario_id'],
            'cliente_id' => ['required', 'exists:clientes,cliente_id'],
        ];
    }
}
