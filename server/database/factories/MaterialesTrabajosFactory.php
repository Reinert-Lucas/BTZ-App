<?php

namespace Database\Factories;

use App\Models\Material;
use App\Models\MaterialesTrabajos;
use App\Models\Trabajo;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<MaterialesTrabajos>
 */
class MaterialesTrabajosFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * En el flujo normal de la app, esta tabla se llena con
     * $trabajo->materiales()->attach(...) (ver TrabajoSeeder), no creando
     * este modelo directamente. Este factory es útil para tests unitarios
     * puntuales sobre el pivote.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'material_id' => Material::factory(),
            'trabajo_id' => Trabajo::factory(),
            'cantidad' => fake()->numberBetween(1, 10),
        ];
    }
}