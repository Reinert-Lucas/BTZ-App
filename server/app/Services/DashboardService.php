<?php

namespace App\Services;

use App\Models\MaterialesTrabajos;
use App\Models\Trabajo;
use App\Models\Usuario;

class DashboardService
{
    private const DIAS = 7;

    /** Traer métricas a mostrar en el dashboard */
    public function getDashboardMetricas(): array
    {
        return [
            'trabajosRealizados' => [
                'type' => 'trabajos',
                'label' => 'Últimos 5 Trabajos Realizados',
                'content' => $this->ultimosTrabajos(),
            ],
            'usuariosActivos' => [
                'type' => 'usuarios',
                'label' => 'Operarios con más Trabajos Realizados (últimos 7 días)',
                'content' => $this->usuariosMasActivos(),
            ],
            'materialesUsados' => [
                'type' => 'materiales',
                'label' => 'Materiales más Usados (últimos 7 días)',
                'content' => $this->materialesMasUsados(),
            ],
        ];
    }

    /** Últimos 5 trabajos realizados */
    private function ultimosTrabajos()
    {
        return Trabajo::with('aviso')->latest()->take(5)->get();
    }

    /** Operarios con más trabajos CARGADOS en los últimos 7 días (no por fecha agendada). */
    private function usuariosMasActivos()
    {
        $desde = now()->subDays(self::DIAS);

        return Usuario::where('rol', 'operario')
            ->withCount([
                'aviso as avisos_finalizados_count' => fn($query) => $query
                    ->where('estado', 'finalizado')
                    ->whereHas('trabajo', fn($t) => $t->where('created_at', '>=', $desde)),
            ])
            ->having('avisos_finalizados_count', '>', 0)
            ->orderByDesc('avisos_finalizados_count')
            ->take(5)
            ->get();
    }

    /** Materiales más usados de los últimos 7 días */
    private function materialesMasUsados()
    {
        return MaterialesTrabajos::selectRaw('material_id, SUM(cantidad) as total')
            ->where('created_at', '>=', now()->subDays(self::DIAS))
            ->groupBy('material_id')
            ->with('material')
            ->orderByDesc('total')
            ->take(5)
            ->get();
    }
}
