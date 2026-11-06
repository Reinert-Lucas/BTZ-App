<?php

namespace App\Http\Requests;

use App\Models\Aviso;
use Illuminate\Contracts\Validation\ValidationRule;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class AvisoRequest extends FormRequest
{
    public function authorize(): bool
    {
        // El acceso se restringe con auth:sanctum / auth + isAdmin en las rutas.
        return true;
    }

    /**
     * @return array<string, ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        /** @var Aviso|null $aviso */
        $aviso = $this->route('aviso'); // null al crear, modelo al actualizar

        return [
            // Formato estricto: 'date' aceptaba textos como "tomorrow" y MySQL fallaba con un 500.
            'fecha' => ['required', 'date_format:Y-m-d'],
            'hora' => ['required', 'date_format:H:i,H:i:s'],
            'direccion' => ['required', 'string', 'max:100'],
            'telefono' => ['required', 'string', 'max:20'],
            'mensaje' => ['nullable', 'string', 'max:255'],
            'observacion' => ['nullable', 'string', 'max:255'],
            // Estado obligatorio solo al actualizar.
            'estado' => [
                Rule::requiredIf($aviso !== null),
                Rule::in(['pendiente', 'finalizado', 'cancelado']),
            ],
            'urgencia' => ['required', Rule::in(['urgente', 'media', 'baja'])],
            // Solo operarios activos, salvo el que el aviso ya tiene asignado (historial).
            'usuario_id' => [
                'required',
                'integer',
                Rule::exists('usuarios', 'usuario_id')->where(
                    fn($query) => $query->where('rol', 'operario')->where(
                        fn($q) => $q->whereNull('deleted_at')
                            ->when($aviso, fn($q2) => $q2->orWhere('usuario_id', $aviso->usuario_id))
                    )
                ),
            ],
            // Solo clientes activos, salvo el que el aviso ya tiene asignado.
            'cliente_id' => [
                'required',
                'integer',
                Rule::exists('clientes', 'cliente_id')->where(
                    fn($query) => $query->where(
                        fn($q) => $q->whereNull('deleted_at')
                            ->when($aviso, fn($q2) => $q2->orWhere('cliente_id', $aviso->cliente_id))
                    )
                ),
            ],
        ];
    }
}
