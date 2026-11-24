<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (Schema::getConnection()->getDriverName() === 'sqlite') {
            return;
        }

        Schema::table('avisos', function (Blueprint $table) {
            $table->string('direccion', 255)->change();
            $table->string('telefono', 50)->change();
        });
    }

    public function down(): void
    {
        if (Schema::getConnection()->getDriverName() === 'sqlite') {
            return;
        }

        Schema::table('avisos', function (Blueprint $table) {
            $table->string('direccion', 100)->change();
            $table->string('telefono', 20)->change();
        });
    }
};
