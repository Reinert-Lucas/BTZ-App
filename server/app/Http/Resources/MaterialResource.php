<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class MaterialResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id' => $this->material_id,
            'nombre' => $this->nombre,
            'detalle' => $this->detalle,
            // Solo aparece cuando el material viene desde la relación
            'cantidad' => $this->whenPivotLoaded(
                'materiales_trabajos',
                fn() => $this->pivot->cantidad
            ),
        ];
    }
}