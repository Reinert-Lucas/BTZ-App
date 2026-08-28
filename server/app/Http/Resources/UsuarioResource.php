<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class UsuarioResource extends JsonResource
{
    /**
     * Transform the resource into an array.
     *
     * @return array<string, mixed>
     */
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->usuario_id,
            'nombre' => $this->nombre,
            'rol' => $this->rol,
            'dni' => $this->dni,
            'telefono' => $this->telefono,
        ];
    }
}
