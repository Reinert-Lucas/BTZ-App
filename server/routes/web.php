<?php

use App\Http\Controllers\DashboardController;
use App\Http\Controllers\WebAuthController;
use App\Http\Controllers\WebAvisoController;
use App\Http\Controllers\WebClienteController;
use App\Http\Controllers\WebMaterialController;
use App\Http\Controllers\WebUsuarioController;
use Illuminate\Support\Facades\Route;

Route::get('/', fn() => view('index'))->name("login");
Route::post('/login', [WebAuthController::class, 'login'])->middleware('throttle:5,1')->name('admin.login');
Route::get('/noaccess', fn() => view('noaccess'))->name('admin.noaccess');
Route::middleware(['auth'])->post('/logout', [WebAuthController::class, 'logout'])->name('admin.logout');
Route::middleware(['auth', 'isAdmin'])->prefix('admin')->name('admin.')->group(function () {
    Route::get('/me', [WebAuthController::class, 'me'])->name('me');
    Route::get('/', [DashboardController::class, 'index'])->name("dashboard");
    Route::resource('usuarios', WebUsuarioController::class);
    Route::resource('clientes', WebClienteController::class);
    Route::resource('avisos', WebAvisoController::class);
    Route::prefix('materiales')->group(function () {
        // No usamos resource aca porque convierte Materiales a Materiale y no a Material
        Route::get('/', [WebMaterialController::class, 'index'])->name('materiales.index');
        Route::get('/create', [WebMaterialController::class, 'create'])->name('materiales.create');
        Route::post('/', [WebMaterialController::class, 'store'])->name('materiales.store');
        Route::get('/{material}/edit', [WebMaterialController::class, 'edit'])->name('materiales.edit');
        Route::put('/{material}', [WebMaterialController::class, 'update'])->name('materiales.update');
        Route::delete('/{material}', [WebMaterialController::class, 'destroy'])->name('materiales.destroy');
    });
    Route::get('/trabajos/{aviso}', [WebAvisoController::class, 'show'])->name('trabajos.show');
});