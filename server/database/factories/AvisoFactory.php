<?php

namespace Database\Factories;

use App\Models\Aviso;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Aviso>
 */
class AvisoFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'fecha' => $this->faker->date(),
            'hora' => $this->faker->time(),
            'direccion' => $this->faker->address(),
            'telefono' => $this->faker->phoneNumber(),
            'mensaje' => $this->faker->sentence(),
            'observacion' => $this->faker->sentence(),
            'estado' => $this->faker->randomElement(['pendiente', 'finalizado', 'cancelado']),
            'urgencia' => $this->faker->randomElement(['urgente', 'media', 'baja']),
            'usuario_id' => \App\Models\Usuario::factory(),
            'cliente_id' => \App\Models\Cliente::factory(),
        ];
    }
}
