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
        Schema::create('avisos', function (Blueprint $table) {
            $table->unsignedBigInteger('aviso_id')->autoIncrement();
            $table->date('fecha');
            $table->time('hora');
            $table->string('direccion', 50);
            $table->string('telefono', 20);
            $table->text('mensaje');
            $table->text('observacion');
            $table->enum('estado', ['pendiente', 'finalizado']);
            $table->enum('urgencia', ['urgente', 'media', 'baja']);
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('avisos');
    }
};
