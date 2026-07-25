<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class TrabajoResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->trabajo_id,
            'trabajo_realizado' => $this->trabajo_realizado,
            'desperfecto' => $this->desperfecto,
            'aviso_id' => $this->aviso_id,
            'materiales' => MaterialResource::collection(
                $this->whenLoaded('materiales')
            ),
        ];
    }
}