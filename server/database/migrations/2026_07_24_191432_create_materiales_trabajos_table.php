<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('materiales_trabajos', function (Blueprint $table) {
            $table->id('materiales_trabajos_id');
            $table->foreignId('material_id')->constrained('materiales', 'material_id');
            $table->foreignId('trabajo_id')->constrained('trabajos', 'trabajo_id');
            $table->integer('cantidad')->unsigned();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('materiales_trabajos');
    }
};
