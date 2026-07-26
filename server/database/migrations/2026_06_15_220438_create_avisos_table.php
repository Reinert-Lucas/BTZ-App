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
        Schema::create('avisos', function (Blueprint $table) {
            $table->id('aviso_id')->autoIncrement();
            $table->date('fecha');
            $table->time('hora');
            $table->string('direccion', 100);
            $table->string('telefono', 20);
            $table->text('mensaje');
            $table->text('observacion');
            $table->enum('estado', ['pendiente', 'finalizado', 'cancelado'])->default('pendiente');
            $table->enum('urgencia', ['urgente', 'media', 'baja']);
            $table->foreignId('usuario_id')->constrained('usuarios', 'usuario_id')->cascadeOnDelete();
            $table->foreignId('cliente_id')->constrained('clientes', 'cliente_id')->cascadeOnDelete();
            $table->softDeletes('deleted_at', 0);
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
