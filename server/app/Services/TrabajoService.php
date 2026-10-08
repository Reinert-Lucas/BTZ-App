<?php

namespace App\Services;

use App\Models\Aviso;

class TrabajoService
{
    public function trabajosAsignados(int $usuario_id)
    {
        return Aviso::with(['usuario', 'cliente'])
            ->where('usuario_id', $usuario_id)
            ->where('estado', 'pendiente')
            ->orderBy('fecha')
            ->orderBy('hora')
            ->orderByRaw("
                CASE urgencia
                    WHEN 'urgente' THEN 1
                    WHEN 'media' THEN 2
                    WHEN 'baja' THEN 3
                    ELSE 4
                END
            ")
            ->paginate(10);
    }

    public function trabajosFinalizados(?int $usuario_id = null)
    {
        $query = Aviso::with(['usuario', 'cliente', 'trabajo.materiales'])
            ->where('estado', 'finalizado');

        if ($usuario_id) {
            $query->where('usuario_id', $usuario_id);
        }

        return $query
            ->orderBy('fecha', 'desc')
            ->orderBy('hora', 'desc')
            ->paginate(10);
    }
}