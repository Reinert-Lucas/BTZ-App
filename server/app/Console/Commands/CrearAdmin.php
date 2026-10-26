<?php

namespace App\Console\Commands;

use App\Models\Usuario;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\Rule;
use Illuminate\Validation\Rules\Password;

class CrearAdmin extends Command
{
    protected $signature = 'usuarios:crear-admin {dni} {nombre} {telefono}';

    protected $description = 'Crea un usuario administrador (la contraseña se pide de forma oculta).';

    public function handle(): int
    {
        $validator = Validator::make([
            'dni' => $this->argument('dni'),
            'nombre' => $this->argument('nombre'),
            'telefono' => $this->argument('telefono'),
            'password' => $this->secret('Contraseña (mínimo 8 caracteres)'),
        ], [
            'dni' => ['required', 'digits_between:7,8', Rule::unique('usuarios', 'dni')],
            'nombre' => ['required', 'string', 'max:60'],
            'telefono' => ['required', 'string', 'max:20'],
            'password' => ['required', Password::min(8)],
        ]);

        if ($validator->fails()) {
            foreach ($validator->errors()->all() as $error) {
                $this->error($error);
            }

            return self::FAILURE;
        }

        Usuario::create($validator->validated() + ['rol' => 'admin']);

        $this->info('Administrador creado correctamente.');

        return self::SUCCESS;
    }
}