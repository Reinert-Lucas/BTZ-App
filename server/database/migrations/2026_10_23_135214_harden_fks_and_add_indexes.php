<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration {
    public function up(): void
    {
        Schema::table('avisos', function (Blueprint $table) {
            $table->dropForeign(['usuario_id']);
            $table->dropForeign(['cliente_id']);
            $table->foreign('usuario_id')->references('usuario_id')->on('usuarios')->restrictOnDelete();
            $table->foreign('cliente_id')->references('cliente_id')->on('clientes')->restrictOnDelete();
            $table->index(['usuario_id', 'estado', 'fecha', 'hora'], 'avisos_usuario_estado_fecha_idx');
            $table->index(['estado', 'fecha'], 'avisos_estado_fecha_idx');
        });

        Schema::table('trabajos', function (Blueprint $table) {
            $table->dropForeign(['aviso_id']);
            $table->foreign('aviso_id')->references('aviso_id')->on('avisos')->restrictOnDelete();
        });

        Schema::table('materiales_trabajos', function (Blueprint $table) {
            $table->dropForeign(['material_id']);
            $table->dropForeign(['trabajo_id']);
            $table->foreign('material_id')->references('material_id')->on('materiales')->restrictOnDelete();
            $table->foreign('trabajo_id')->references('trabajo_id')->on('trabajos')->restrictOnDelete();
            $table->unique(['trabajo_id', 'material_id'], 'mat_trab_unique');
        });
    }

    public function down(): void
    {
        Schema::table('materiales_trabajos', function (Blueprint $table) {
            $table->dropForeign(['material_id']);
            $table->dropForeign(['trabajo_id']);
            $table->dropUnique('mat_trab_unique');
            $table->foreign('material_id')->references('material_id')->on('materiales')->cascadeOnDelete();
            $table->foreign('trabajo_id')->references('trabajo_id')->on('trabajos')->cascadeOnDelete();
        });

        Schema::table('trabajos', function (Blueprint $table) {
            $table->dropForeign(['aviso_id']);
            $table->foreign('aviso_id')->references('aviso_id')->on('avisos')->cascadeOnDelete();
        });

        Schema::table('avisos', function (Blueprint $table) {
            $table->dropIndex('avisos_usuario_estado_fecha_idx');
            $table->dropIndex('avisos_estado_fecha_idx');
            $table->dropForeign(['usuario_id']);
            $table->dropForeign(['cliente_id']);
            $table->foreign('usuario_id')->references('usuario_id')->on('usuarios')->cascadeOnDelete();
            $table->foreign('cliente_id')->references('cliente_id')->on('clientes')->cascadeOnDelete();
        });
    }
};