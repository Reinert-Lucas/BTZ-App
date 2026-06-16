<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('usuarios', function (Blueprint $table) {
            $table->unsignedBigInteger('usuario_id')->autoIncrement();
            $table->string('nombre')->nullable(false);
            $table->string('password');
            $table->string('dni', 8)->unique()->nullable(false);
            $table->string('telefono', 20);
            $table->enum('rol', ['admin', 'operario']);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('usuarios');
    }
};
