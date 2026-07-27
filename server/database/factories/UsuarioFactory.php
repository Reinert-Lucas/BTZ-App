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
            'password' => '123',
            'rol' => fake()->randomElement(
                ['admin', 'operario']
            ),
            'dni' => fake()->unique()->numberBetween('11111111', '99999999'),
            'telefono' => fake()->phoneNumber()
        ];
    }
    public function operario(): static
    {
        return $this->state(fn(array $attributes) => [
            'rol' => 'operario',
        ]);
    }

}
