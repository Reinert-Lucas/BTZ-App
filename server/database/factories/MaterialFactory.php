<?php

namespace Database\Factories;

use App\Models\Material;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends Factory<Material>
 */
class MaterialFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        $materiales = [
            'Cable unipolar 2.5mm' => 'Cable de cobre unipolar, rollo x 100 metros',
            'Cañería PVC 110mm' => 'Caño de PVC para desagüe cloacal, 3 metros',
            'Llave térmica 20A' => 'Interruptor termomagnético monofásico',
            'Grifería monocomando' => 'Grifería de cocina monocomando cromada',
            'Sifón de PVC' => 'Sifón flexible para pileta de cocina',
            'Cinta aisladora' => 'Cinta aisladora de vinilo, rollo x 20 metros',
            'Tomacorriente doble' => 'Tomacorriente doble con puesta a tierra',
            'Soldadura de estaño' => 'Rollo de estaño para soldadura de cañerías de cobre',
            'Válvula esférica 1/2"' => 'Válvula de paso esférica de bronce',
            'Termotanque a gas' => 'Termotanque a gas natural, 80 litros',
            'Flexible de conexión' => 'Flexible de conexión para grifería, 40cm',
            'Disyuntor diferencial' => 'Disyuntor diferencial bipolar 25A - 30mA',
        ];

        $nombre = fake()->randomElement(array_keys($materiales));

        return [
            'nombre' => $nombre,
            'detalle' => $materiales[$nombre],
        ];
    }
}