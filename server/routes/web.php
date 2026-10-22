<?php

use App\Http\Controllers\DashboardController;
use App\Http\Controllers\WebAuthController;
use App\Http\Controllers\WebAvisoController;
use App\Http\Controllers\WebClienteController;
use App\Http\Controllers\WebMaterialController;
use App\Http\Controllers\WebUsuarioController;
use Illuminate\Support\Facades\Route;

/*
WEB ROUTES
    Rutas del panel de administracion, protegidas por el middleware auth, que verifica que el usuario este autenticado con una sesion valida.
    Division por roles:
    - Usuarios autenticados (cualquier rol): pueden acceder a /me, /logout
    - Usuarios con rol admin: pueden acceder a /admin/usuarios, /admin/avisos, /admin/clientes, /admin/materiales
    El frontend en Kotlin se conecta a las rutas definidas en routes/api.php para obtener y enviar datos de la aplicacion movil. Por otro lado, el frontend con Blade se conecta a estas rutas para el panel de administracion.
*/

// Rutas Publicas de auth
Route::get('/', fn() => view('index'))->name("login");
Route::post('/login', [WebAuthController::class, 'login'])->middleware('throttle:5,1')->name('admin.login');
// Pagina de "Sin Acceso" para usuarios que intentan acceder a rutas de admin sin ser admin
Route::get('/noaccess', fn() => view('noaccess'))->name('admin.noaccess');
// Rutas protegidas por auth e isAdmin, para el panel de administracion
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
    Route::get('/trabajos/{aviso}/pdf', [WebAvisoController::class, 'pdf'])->name('trabajos.pdf');
    Route::get('/trabajos/{aviso}', [WebAvisoController::class, 'show'])->name('trabajos.show');
});