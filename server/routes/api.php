<?php

use App\Http\Controllers\AvisoController;
use App\Http\Controllers\UsuarioController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ClienteController;
use App\Http\Controllers\MaterialController;
use App\Http\Controllers\TrabajoController;

// Rutas publicas de auth
// Route::post('/create', [AuthController::class, 'create'])->name('register'); // <--- Ruta innecesaria, el registro se hace desde el panel de administración, no desde la app
Route::post('/login', [AuthController::class, 'login'])->middleware('throttle:5,1')->name('api.login');
// auth:sanctum es el middleware que protege las rutas 
Route::middleware('auth:sanctum')->group(function () {
    // Rutas que solo pueden acceder los usuarios autenticados
    Route::get('/me', [AuthController::class, 'me'])->name('me');
    Route::post('/logout', [AuthController::class, 'logout'])->name('logout');
    // Rutas que solo pueden acceder los usuarios con rol admin
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
        // Ver trabajos finalizados c/materiales usados
        Route::get('/trabajos/finalizados/{usuario_id?}', [TrabajoController::class, 'indexFinalizado'])->name('trabajo.indexFinalizado');
        // ABM Clientes
        Route::get('/clientes', [ClienteController::class, 'index'])->name('cliente.index');
        Route::get('/clientes/{cliente}', [ClienteController::class, 'show'])->name('cliente.show');
        Route::post('/clientes', [ClienteController::class, 'store'])->name('cliente.store');
        Route::put('/clientes/{cliente}', [ClienteController::class, 'update'])->name('cliente.update');
        Route::delete('/clientes/{cliente}', [ClienteController::class, 'delete'])->name('cliente.delete');
        // ABM Materiales
        Route::get('/materiales/{material}', [MaterialController::class, 'show'])->name('material.show');
        Route::post('/materiales', [MaterialController::class, 'store'])->name('material.store');
        Route::put('/materiales/{material}', [MaterialController::class, 'update'])->name('material.update');
        Route::delete('/materiales/{material}', [MaterialController::class, 'delete'])->name('material.delete');
    });
    // Publica para poder seleccionar materiales
    Route::get('/materiales', [MaterialController::class, 'index'])->name('material.index');
    // Rutas de operarios
    // Traer trabajos asignados a un operario
    Route::get('/trabajos', [TrabajoController::class, 'index'])->name('trabajo.index');
    // Cargar datos del trabajo 
    Route::post('/trabajos', [TrabajoController::class, 'store'])->name('trabajo.store');
});
