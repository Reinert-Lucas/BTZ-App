<?php

namespace Database\Seeders;

use App\Models\Aviso;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class AvisoSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        Aviso::factory(12)->pendiente()->create();
        Aviso::factory(3)->cancelado()->create();
    }
}
