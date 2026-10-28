<?php
namespace App\Services;

use App\Models\MaterialesTrabajos;
use App\Models\Trabajo;
use App\Models\Usuario;

class DashboardService
{
    /** Traer metricas a mostrar en el dashboard */
    public function getDashboardMetricas()
    {
        return [
            'trabajosRelizados' => [
                'type' => "trabajos",
                'label' => 'Ultimos 5 Trabajos Realizados',
                'content' => $this->ultimosTrabajos(),
            ],
            'usuariosActivos' => [
                'type' => "usuarios",
                'label' => 'Usuarios con más Trabajos Relizados',
                'content' => $this->usuariosMasActivos()
            ],
            'materialesUsados' => [
                'type' => "materiales",
                'label' => 'Materiales mas Usados',
                'content' => $this->materialesMasUsados(),
            ]
        ];
    }
    /** Ultimos 5 trabajos realizados */
    private function ultimosTrabajos()
    {
        return Trabajo::with(['aviso'])->latest()->take(5)->get();
    }
    /**
     * Usuarios más activos de los últimos 7 días
     */
    private function usuariosMasActivos()
    {
        $desde = now()->subDays(7);

        return Usuario::where('rol', 'operario')
            ->withCount([
                'aviso as avisos_finalizados_count' => fn($query) => $query
                    ->where('estado', 'finalizado')
                    ->where('fecha', '>=', $desde),
            ])
            ->orderByDesc('avisos_finalizados_count')
            ->take(5)
            ->get();
    }

    /**
     * Materiales más usados de los últimos 7 días
     */
    private function materialesMasUsados()
    {
        return MaterialesTrabajos::selectRaw(
            'material_id, SUM(cantidad) as total'
        )
            ->where('created_at', '>=', now()->subDays(7))
            ->groupBy('material_id')
            ->with('material')
            ->orderByDesc('total')
            ->take(5)
            ->get();
    }
}