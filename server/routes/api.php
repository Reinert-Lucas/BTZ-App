<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;

// Rutas publicas de auth
// Route::post('/create', [AuthController::class, 'create'])->name('register'); // <--- Ruta innecesaria, el registro se hace desde el panel de administración, no desde la app
Route::post('/login', [AuthController::class, 'login'])->name('login');
// auth:sanctum es el middleware que protege las rutas 
Route::middleware('auth:sanctum')->group(function () {
    // Pone aca las rutas que solo pueden acceder los usuarios autenticados
    Route::get('/me', [AuthController::class, 'me'])->name('me');
    Route::post('/logout', [AuthController::class, 'logout'])->name('logout');
});
