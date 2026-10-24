<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class AvisoRequest extends FormRequest
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
            'fecha' => ['required', 'date'],
            'hora' => ['required', 'date_format:H:i,H:i:s'],
            'direccion' => ['required', 'string', 'max:100'],
            'telefono' => ['required', 'string', 'max:20'],
            'mensaje' => ['nullable', 'string', 'max:255'],
            'observacion' => ['nullable', 'string', 'max:255'],
            // Estado obligatorio solo al actualizar.
            'estado' => [
                Rule::requiredIf($this->isMethod('put') || $this->isMethod('patch')),
                Rule::in(['pendiente', 'finalizado', 'cancelado']),
            ],
            'urgencia' => ['required', Rule::in(['urgente', 'media', 'baja'])],
            // Solo operarios activos pueden recibir avisos.
            'usuario_id' => [
                'required',
                'integer',
                Rule::exists('usuarios', 'usuario_id')->where('rol', 'operario')->whereNull('deleted_at'),
            ],
            'cliente_id' => [
                'required',
                'integer',
                Rule::exists('clientes', 'cliente_id')->whereNull('deleted_at'),
            ],
        ];
    }
}