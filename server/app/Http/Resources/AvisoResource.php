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
            'id' => $this->aviso_id,
            'fecha' => $this->fecha,
            'hora' => $this->hora,
            'direccion' => $this->direccion,
            'telefono' => $this->telefono,
            'mensaje' => $this->mensaje,
            'observacion' => $this->observacion,
            'estado' => $this->estado,
            'urgencia' => $this->urgencia,
            'operario' => [
                'id' => $this->usuario->usuario_id,
                'nombre' => $this->usuario->nombre,
                'telefono' => $this->usuario->telefono
            ],
            'cliente' => [
                'id' => $this->cliente->cliente_id,
                'nombre' => $this->cliente->nombre,
                'asegurado' => $this->cliente->asegurado,
                'asegurado_detalle' => $this->cliente->asegurado_detalle,
            ],
            'trabajo' => new TrabajoResource($this->whenLoaded('trabajo'))
        ];
    }
}
