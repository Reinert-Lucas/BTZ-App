<?php

use App\Http\Controllers\AvisoController;
use App\Http\Controllers\UsuarioController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;

// Rutas publicas de auth
// Route::post('/create', [AuthController::class, 'create'])->name('register'); // <--- Ruta innecesaria, el registro se hace desde el panel de administración, no desde la app
Route::post('/login', [AuthController::class, 'login'])->name('login');
// auth:sanctum es el middleware que protege las rutas 
Route::middleware('auth:sanctum')->group(function () {
    //  Rutas que solo pueden acceder los usuarios autenticados
    Route::get('/me', [AuthController::class, 'me'])->name('me');
    Route::post('/logout', [AuthController::class, 'logout'])->name('logout');
    Route::middleware('isAdmin')->group(function () {
        // ABM Usuarios (Operarios)
        Route::get('/usuarios', [UsuarioController::class, 'index'])->name('usuario.index');
        Route::get('/usuarios/{usuario}', [UsuarioController::class, 'show'])->name('usuario.show');
        Route::post('/usuarios', [UsuarioController::class, 'store'])->name('usuario.store');
        Route::put('/usuarios/{usuario}', [UsuarioController::class, 'update'])->name('usuario.update');
        Route::delete('/usuarios/{usuario}', [UsuarioController::class, 'delete'])->name('usuario.delete');
        // ABM Avisos
        Route::get('/avisos', [AvisoController::class, 'index'])->name('aviso.index');
        Route::get('/avisos/{aviso}', [AvisoController::class, 'show'])->name('aviso.show');
        Route::post('/avisos', [AvisoController::class, 'store'])->name('aviso.store');
        Route::put('/avisos/{aviso}', [AvisoController::class, 'update'])->name('aviso.update');
        Route::delete('/avisos/{aviso}', [AvisoController::class, 'delete'])->name('aviso.delete');
    });
});
