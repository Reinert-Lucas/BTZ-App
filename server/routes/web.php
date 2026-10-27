<?php

use App\Http\Controllers\DashboardController;
use App\Http\Controllers\WebAuthController;
use App\Http\Controllers\WebAvisoController;
use App\Http\Controllers\WebClienteController;
use App\Http\Controllers\WebMaterialController;
use App\Http\Controllers\WebUsuarioController;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Route;

/*
WEB ROUTES
    Rutas del panel de administracion, protegidas por el middleware auth, que verifica que el usuario este autenticado con una sesion valida.
    Division por roles:
    - Usuarios autenticados (cualquier rol): pueden acceder a /me, /logout
    - Usuarios con rol admin: pueden acceder a /admin/usuarios, /admin/avisos, /admin/clientes, /admin/materiales
    El frontend en Kotlin se conecta a las rutas definidas en routes/api.php para obtener y enviar datos de la aplicacion movil. Por otro lado, el frontend con Blade se conecta a estas rutas para el panel de administracion.
*/
// Routes [Direccion] <=> Controller [Filtros] <=> Service [Logica de Negocio] <=> DB [Persistencia]

// Login: si ya hay sesión se redirige.
Route::get('/', fn() => Auth::check()
    ? redirect()->route('admin.dashboard')
    : view('index'))->name('login');
Route::post('/login', [WebAuthController::class, 'login'])->middleware('throttle:login')->name('admin.login');
// Página de "Sin Acceso" para usuarios que intentan acceder a rutas de admin sin ser admin
Route::get('/noaccess', fn() => view('noaccess'))->name('admin.noaccess');
Route::middleware(['auth'])->post('/logout', [WebAuthController::class, 'logout'])->name('admin.logout');
Route::middleware(['auth', 'isAdmin'])->prefix('admin')->name('admin.')->group(function () {
    Route::get('/me', [WebAuthController::class, 'me'])->name('me');
    Route::get('/', [DashboardController::class, 'index'])->name('dashboard');
    // Sin 'show': los controladores web no lo implementan.
    Route::resource('usuarios', WebUsuarioController::class)->except('show');
    Route::resource('clientes', WebClienteController::class)->except('show');
    Route::resource('avisos', WebAvisoController::class)->except('show');
    Route::resource('materiales', WebMaterialController::class)->parameters(['materiales' => 'material'])->except('show');
    Route::get('/trabajos/{aviso}/pdf', [WebAvisoController::class, 'pdf'])->name('trabajos.pdf');
    Route::get('/trabajos/{aviso}', [WebAvisoController::class, 'show'])->name('trabajos.show');
});