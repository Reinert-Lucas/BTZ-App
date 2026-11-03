<?php

namespace App\Services;

use App\Models\MaterialesTrabajos;
use App\Models\Trabajo;
use App\Models\Usuario;

class DashboardService
{
    private const DIAS_METRICAS = 7;

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
                'label' => 'Usuarios con más Trabajos Realizados',
                'content' => $this->usuariosMasActivos(),
            ],
            'materialesUsados' => [
                'type' => 'materiales',
                'label' => 'Materiales más Usados',
                'content' => $this->materialesMasUsados(),
            ],
        ];
    }

    /** Últimos 5 trabajos realizados */
    private function ultimosTrabajos()
    {
        return Trabajo::with('aviso')->latest()->take(5)->get();
    }

    /** Operarios con más trabajos CARGADOS en los últimos 7 días (por fecha de carga, no del aviso). */
    private function usuariosMasActivos()
    {
        $desde = now()->subDays(self::DIAS_METRICAS);

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

    /** Materiales más usados en los últimos 7 días */
    private function materialesMasUsados()
    {
        return MaterialesTrabajos::selectRaw('material_id, SUM(cantidad) as total')
            ->where('created_at', '>=', now()->subDays(self::DIAS_METRICAS))
            ->groupBy('material_id')
            ->with('material')
            ->orderByDesc('total')
            ->take(5)
            ->get();
    }
}