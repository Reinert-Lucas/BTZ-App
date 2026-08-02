<?php

use App\Http\Controllers\WebAuthController;
use App\Http\Controllers\WebAvisoController;
use App\Http\Controllers\WebClienteController;
use App\Http\Controllers\WebMaterialController;
use App\Http\Controllers\WebUsuarioController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn() => view('index'))->name("login");
Route::post('/login', [WebAuthController::class, 'login'])->name('admin.login');
Route::get('/noaccess', fn() => view('noaccess'))->name('admin.noaccess');
Route::middleware(['auth'])->post('/logout', [WebAuthController::class, 'logout'])->name('admin.logout');
Route::middleware(['auth', 'isAdmin'])->prefix('admin')->name('admin.')->group(function () {
    Route::get('/me', [WebAuthController::class, 'me'])->name('me');
    Route::get('/', fn() => view('admin.dashboard'))->name("dashboard");
    Route::resource('usuarios', WebUsuarioController::class);
    Route::resource('clientes', WebClienteController::class);
    Route::resource('avisos', WebAvisoController::class);
    // No usamos resource aca porque convierte Materiales a Materiale y no a Material
    Route::get('/materiales', [WebMaterialController::class, 'index'])->name('materiales.index');
    Route::get('/materiales/create', [WebMaterialController::class, 'create'])->name('materiales.create');
    Route::post('/materiales', [WebMaterialController::class, 'store'])->name('materiales.store');
    Route::get('/materiales/{material}/edit', [WebMaterialController::class, 'edit'])->name('materiales.edit');
    Route::put('/materiales/{material}', [WebMaterialController::class, 'update'])->name('materiales.update');
    Route::delete('/materiales/{material}', [WebMaterialController::class, 'destroy'])->name('materiales.destroy');
    Route::get('/trabajos/{aviso}', [WebAvisoController::class, 'show'])->name('trabajos.show');
});