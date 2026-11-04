<?php

use App\Http\Controllers\AvisoController;
use App\Http\Controllers\UsuarioController;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\AuthController;
use App\Http\Controllers\ClienteController;
use App\Http\Controllers\MaterialController;
use App\Http\Controllers\TrabajoController;

/*
API ROUTES
    Rutas de la API Rest, protegidas por el middleware auth:sanctum, que verifica que el usuario este autenticado con un token valido.
    Division por roles:
    - Usuarios autenticados (cualquier rol): pueden acceder a /me, /logout, /fcm-token, /trabajos, /trabajos/finalizados, /materiales
    - Usuarios con rol admin: pueden acceder a /usuarios, /avisos, /clientes, /materiales
    El frontend en Kotlin se conecta a estas rutas para obtener y enviar datos de la aplicacion movil. Por otro lado, el frontend con Blade se conecta a las rutas definidas en routes/web.php para el panel de administracion.
*/

// Routes [Direccion] <=> Controller [Filtros] <=> Service [Logica de Negocio] <=> DB [Persistencia]
// Rutas publicas de auth
Route::post('/login', [AuthController::class, 'login'])->middleware('throttle:login')->name('api.login');
// auth:sanctum es el middleware que protege las rutas 
Route::middleware('auth:sanctum')->group(function () {
    // Rutas a las que pueden acceder usuarios autenticados
    Route::get('/me', [AuthController::class, 'me'])->name('me');
    Route::post('/logout', [AuthController::class, 'logout'])->name('logout');
    Route::post('/fcm-token', [UsuarioController::class, 'saveFcmToken'])->name('usuario.saveFcmToken'); // <-- Ruta para guardar el token FCM del usuario autenticado
    Route::get('/avisos', [AvisoController::class, 'index'])
        ->name('avisos.index');
    // Rutas que solo pueden acceder los usuarios con rol admin
    Route::middleware('isAdmin')->group(function () {
        /* 
            ABM's del Panel de Administracion
            ::resource() resume todas las rutas de un CRUD (index, show, store, update, destroy)
            Son las mismas rutas que se definieron antes, pero de forma mas resumida
            Ej de las rutas que genera Route::resource('usuarios', UsuarioController::class):
                GET /usuarios -> index
                GET /usuarios/{usuario} -> show
                POST /usuarios -> store
                PUT o PATCH /usuarios/{usuario} -> update
                DELETE /usuarios/{usuario} -> destroy
        */
        Route::apiResource('usuarios', UsuarioController::class);
        Route::apiResource('avisos', AvisoController::class)->except(['index']);
        Route::apiResource('clientes', ClienteController::class);
        Route::apiResource('materiales', MaterialController::class)->parameters(['materiales' => 'material'])->except(['index']);
    });
    // Publica para poder seleccionar materiales
    Route::get('/materiales', [MaterialController::class, 'index'])->name('material.index');
    // Ver trabajos finalizados c/materiales usados
    Route::get('/trabajos/finalizados/{usuario_id?}', [TrabajoController::class, 'indexFinalizado'])->name('trabajo.indexFinalizado');
    // Traer trabajos asignados a un operario
    Route::get('/trabajos', [TrabajoController::class, 'index'])->name('trabajo.index');
    // Crear datos de un trabajo finalizado
    Route::post('/trabajos', [TrabajoController::class, 'store'])->name('trabajo.store');
});
