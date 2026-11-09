<?php

namespace App\Http\Requests;

use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

/**
 * Valida los filtros de los listados (API y web).
 * Evita que ?nombre[]=x llegue como array a los servicios
 * ("Array to string conversion" / TypeError => 500).
 */
class ListadoRequest extends FormRequest
{
    public function authorize(): bool
    {
        // El acceso lo controlan auth:sanctum / auth + isAdmin en las rutas.
        return true;
    }

    /**
     * @return array<string, ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        return [
            'page' => ['nullable', 'integer', 'min:1'],
            'buscar' => ['nullable', 'string', 'max:100'],
            'nombre' => ['nullable', 'string', 'max:100'],
            'email' => ['nullable', 'string', 'max:50'],
            'dni' => ['nullable', 'string', 'max:9'],
            'rol' => ['nullable', Rule::in(['admin', 'operario'])],
            'direccion' => ['nullable', 'string', 'max:100'],
            'telefono' => ['nullable', 'string', 'max:20'],
            'asegurado_detalle' => ['nullable', 'string', 'max:255'],
            'fecha' => ['nullable', 'date_format:Y-m-d'],
            'hora' => ['nullable', 'date_format:H:i,H:i:s'],
            'estado' => ['nullable', Rule::in(['pendiente', 'finalizado', 'cancelado'])],
            'urgencia' => ['nullable', Rule::in(['urgente', 'media', 'baja'])],
            'aviso_id' => ['nullable', 'integer', 'min:1'],
            'cliente_id' => ['nullable', 'integer', 'min:1'],
            'usuario_id' => ['nullable', 'integer', 'min:1'],
            'material_id' => ['nullable', 'integer', 'min:1'],
            // Retrofit envía "true"/"false"; la regla 'boolean' no los acepta.
            'asegurado' => ['nullable', 'in:0,1,true,false'],
            'eliminados' => ['nullable', 'in:0,1,true,false'],
        ];
    }
}
