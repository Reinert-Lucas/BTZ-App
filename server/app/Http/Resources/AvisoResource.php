<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class AvisoResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
            'aviso_id' => $this->aviso_id,
            'fecha' => $this->fecha,
            'hora' => $this->hora,
            'direccion' => $this->direccion,
            'telefono' => $this->telefono,
            'mensaje' => $this->mensaje,
            'observacion' => $this->observacion,
            'estado' => $this->estado,
            'urgencia' => $this->urgencia,
            'operario' => [
                'nombre' => $this->usuario->nombre,
                'dni' => $this->usuario->dni,
                'telefono' => $this->usuario->telefono
            ],
            'cliente' => [
                'nombre' => $this->cliente->nombre,
                'asegurado' => $this->cliente->asegurado,
                'asegurado_detalle' => $this->cliente->asegurado_detalle,
            ]
        ];
    }
}
