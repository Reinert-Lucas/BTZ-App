<?php

namespace Database\Seeders;

use App\Models\Usuario;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class UsuarioSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        if (app()->isProduction()) {
            return;
        }
        if (!app()->environment(['local', 'testing'])) {
            return;
        }

        Usuario::factory(10)->create();
        Usuario::create([
            'nombre' => 'admin',
            'password' => '123',
            'rol' => 'admin',
            'dni' => 'admin',
            'telefono' => '12345',
        ]);
    }
}
