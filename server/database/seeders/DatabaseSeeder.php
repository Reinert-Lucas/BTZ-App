<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    public function run(): void
    {
        // Los seeders son datos de DEMO (faker, admin/123): nunca en producción.
        if (app()->isProduction()) {
            $this->command?->warn('Seeders de demo deshabilitados en producción. Crea el admin real con un comando Artisan.');
            return;
        }
        if (!app()->environment(['local', 'testing'])) {
            return;
        }

        $this->call([
            UsuarioSeeder::class,
            ClienteSeeder::class,
            MaterialSeeder::class,
            AvisoSeeder::class,
            TrabajoSeeder::class,
        ]);
    }
}