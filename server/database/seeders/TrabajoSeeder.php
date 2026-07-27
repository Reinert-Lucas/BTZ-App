<?php

namespace Database\Seeders;

use App\Models\Aviso;
use App\Models\Material;
use App\Models\Trabajo;
use Illuminate\Database\Seeder;

class TrabajoSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * Reproduce el flujo real (TrabajoController::store): toma avisos
     * 'pendiente' ya existentes, los marca 'finalizado' y les crea su
     * Trabajo con 1 a 3 materiales asociados (con cantidad), respetando
     * la restricción unique de trabajos.aviso_id.
     */
    public function run(): void
    {
        $materiales = Material::all();

        if ($materiales->isEmpty()) {
            $materiales = Material::factory(12)->create();
        }

        Aviso::where('estado', 'pendiente')
            ->inRandomOrder()
            ->limit(5)
            ->get()
            ->each(function (Aviso $aviso) use ($materiales) {
                $aviso->update(['estado' => 'finalizado']);

                $trabajo = Trabajo::factory()->create([
                    'aviso_id' => $aviso->aviso_id,
                ]);

                $materiales->random(random_int(1, 3))->each(
                    fn(Material $material) => $trabajo->materiales()->attach(
                        $material->material_id,
                        ['cantidad' => random_int(1, 5)]
                    )
                );
            });
    }
}