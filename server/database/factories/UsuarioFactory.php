<?php

namespace Database\Factories;

use App\Models\Usuario;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Usuario>
 */
class UsuarioFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'nombre' => fake()->name(),
            'password' => '$2y$10$BPmILeM9352hpMzgqqDT8u2EnBa8kjBNinDFz7R/ZbcIRbznJ0BNW',
            'rol' => fake()->randomElement(
                ['admin', 'operario']
            ),
            'dni' => fake()->numberBetween('11111111', '99999999'),
            'telefono' => fake()->phoneNumber(),
        ];
    }
}
