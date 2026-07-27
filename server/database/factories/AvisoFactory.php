<?php

namespace Database\Factories;

use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Usuario;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Aviso>
 */
class AvisoFactory extends Factory
{
    protected $model = Aviso::class;

    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'fecha' => fake()->dateTimeBetween('-2 months', '+1 week')->format('Y-m-d'),
            'hora' => fake()->time('H:i:s'),
            'direccion' => fake()->streetAddress(),
            'telefono' => fake()->phoneNumber(),
            'mensaje' => fake()->randomElement([
                'Pérdida de agua en el baño principal',
                'No funciona ninguna toma de electricidad',
                'Cañería tapada en la cocina',
                'Termotanque no calienta el agua',
                'Corte de luz en el tablero general',
                'Grifería de la cocina pierde agua',
            ]),
            'observacion' => fake()->randomElement([
                'Cliente solicita turno por la mañana',
                'Acceso al edificio por portería',
                'Traer repuestos para grifería monocomando',
                'Cliente presente todo el día',
                'Sin observaciones adicionales',
            ]),
            'estado' => 'pendiente',
            'urgencia' => fake()->randomElement(['urgente', 'media', 'baja']),
            'usuario_id' => Usuario::factory()->operario(),
            'cliente_id' => Cliente::factory(),
        ];
    }

    public function pendiente(): static
    {
        return $this->state(fn(array $attributes) => ['estado' => 'pendiente']);
    }

    public function finalizado(): static
    {
        return $this->state(fn(array $attributes) => ['estado' => 'finalizado']);
    }

    public function cancelado(): static
    {
        return $this->state(fn(array $attributes) => ['estado' => 'cancelado']);
    }
}