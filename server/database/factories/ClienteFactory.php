<?php

namespace Database\Factories;

use App\Models\Cliente;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Cliente>
 */
class ClienteFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        $asegurado = fake()->boolean();
        return [
            'nombre' => $this->faker->name(),
            'email' => $this->faker->unique()->safeEmail(),
            'asegurado' => $asegurado,
            'asegurado_detalle' => $asegurado
                ? fake()->randomElement([
                    'Sancor Seguros - Póliza vigente, cobertura de daños por agua',
                    'La Caja Seguros - Cobertura de instalaciones eléctricas',
                    'Mercantil Andina - Cobertura total del hogar',
                    'Federación Patronal - Cobertura de plomería y gas',
                ])
                : null,

        ];
    }
}
