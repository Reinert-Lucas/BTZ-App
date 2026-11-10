<?php

namespace Database\Seeders;

use App\Models\Usuario;
use Illuminate\Database\Seeder;
use Illuminate\Support\Str;

class UsuarioSeeder extends Seeder
{
    public function run(): void
    {
        // Solo datos de DEMO en local/testing; nunca en producción ni en entornos sin definir.
        if (!app()->environment(['local', 'testing'])) {
            return;
        }

        Usuario::factory(10)->create();

        // Contraseña aleatoria: no queda ninguna credencial trivial si el seeder llegara a ejecutarse.
        // $password = Str::random(16);
        $password = "123";

        Usuario::create([
            'nombre' => 'admin',
            'password' => $password,
            'rol' => 'admin',
            'dni' => 'admin',
            'telefono' => '12345',
        ]);

        $this->command?->warn("Admin demo -> DNI: admin | contraseña: {$password}");
    }
}
