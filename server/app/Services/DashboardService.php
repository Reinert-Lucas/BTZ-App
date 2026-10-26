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

    /** Usuarios con más trabajos realizados */
    private function usuariosMasActivos()
    {
        return Usuario::where('rol', 'operario')
            ->withCount([
                'aviso as avisos_finalizados_count' => fn($query) => $query->where('estado', 'finalizado'),
            ])
            ->orderByDesc('avisos_finalizados_count')
            ->take(5)
            ->get();
    }

    /** Materiales más usados */
    private function materialesMasUsados()
    {
        return MaterialesTrabajos::selectRaw('material_id, SUM(cantidad) as total')
            ->groupBy('material_id')
            ->with('material')
            ->orderByDesc('total')
            ->take(5)
            ->get();
    }
}