<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ClienteResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->cliente_id,
            'nombre' => $this->nombre,
            'email' => $this->email,
            'asegurado' => $this->asegurado,
            'asegurado_detalle' => $this->asegurado_detalle
        ];
    }
}
