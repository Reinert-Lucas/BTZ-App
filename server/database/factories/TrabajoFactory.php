<?php

namespace Database\Factories;

use App\Models\Aviso;
use App\Models\Trabajo;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Trabajo>
 */
class TrabajoFactory extends Factory
{
    protected $model = Trabajo::class;

    /**
     * Define the model's default state.
     *
     * Por defecto genera su propio Aviso en estado 'finalizado', ya que
     * trabajos.aviso_id tiene una restricción unique (1 trabajo por aviso).
     * En los seeders reales conviene pasar 'aviso_id' explícito sobre un
     * aviso ya existente en vez de dejar que cree uno nuevo.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'trabajo_realizado' => fake()->randomElement([
                'Reparación de pérdida de agua en cocina',
                'Instalación de termotanque nuevo',
                'Reemplazo de llave térmica quemada',
                'Destape de cañería cloacal',
                'Cambio de grifería de baño',
                'Instalación de tomacorrientes adicionales',
                'Reparación de disyuntor diferencial',
            ]),
            'desperfecto' => fake()->randomElement([
                'Pérdida de agua constante',
                'Corte de energía en el tablero',
                'Cañería obstruida',
                'Grifería con pérdida',
                'Falta de tomas de electricidad',
                'Disyuntor salta constantemente',
            ]),
            'aviso_id' => Aviso::factory()->finalizado(),
        ];
    }
}