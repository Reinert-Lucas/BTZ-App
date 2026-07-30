<?php

use App\Http\Controllers\WebAvisoController;
use App\Http\Controllers\WebClienteController;
use App\Http\Controllers\WebMaterialController;
use App\Http\Controllers\WebUsuarioController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn() => view('index'))->name("admin.index");
Route::get('/admin', fn() => view('admin.dashboard'))->name("admin.dashboard");
Route::prefix('admin')
    ->name('admin.')
    ->group(function () {
        Route::resource('usuarios', WebUsuarioController::class);
        Route::resource('clientes', WebClienteController::class);
        Route::resource('avisos', WebAvisoController::class);
        // No usamos resource aca porque convierte Materiales a Materiale y no a Material
        Route::get('/materiales', [WebMaterialController::class, 'index'])->name('materiales.index');
        Route::get('/materiales/create', [WebMaterialController::class, 'create'])->name('materiales.create');
        Route::post('/materiales', [WebMaterialController::class, 'store'])->name('materiales.store');
        Route::get('/materiales/{material}/edit', [WebMaterialController::class, 'edit'])->name('materiales.edit');
        Route::put('/materiales/{material}', [WebMaterialController::class, 'update'])->name('materiales.update');
        Route::delete('/materiales/{material}', [WebMaterialController::class, 'index'])->name('materiales.destroy');
    });