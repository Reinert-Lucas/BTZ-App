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

Route::get('/', fn() => Auth::check()
    ? redirect()->route('admin.dashboard')
    : view('index'))->name('login');
Route::post('/login', [WebAuthController::class, 'login'])->middleware('throttle:login')->name('admin.login');
Route::get('/noaccess', fn() => view('noaccess'))->name('admin.noaccess');
Route::middleware(['auth'])->post('/logout', [WebAuthController::class, 'logout'])->name('admin.logout');

Route::middleware(['auth', 'auth.session', 'isAdmin'])->prefix('admin')->name('admin.')->group(function () {
    Route::get('/me', [WebAuthController::class, 'me'])->name('me');
    Route::get('/', [DashboardController::class, 'index'])->name('dashboard');

    Route::resource('usuarios', WebUsuarioController::class)->except('show');
    Route::patch('usuarios/{usuario}/restore', [WebUsuarioController::class, 'restore'])->name('usuarios.restore');
    Route::delete('usuarios/{usuario}/force-delete', [WebUsuarioController::class, 'forceDelete'])->name('usuarios.forceDelete');

    Route::resource('clientes', WebClienteController::class)->except('show');
    Route::patch('clientes/{cliente}/restore', [WebClienteController::class, 'restore'])->name('clientes.restore');
    Route::delete('clientes/{cliente}/force-delete', [WebClienteController::class, 'forceDelete'])->name('clientes.forceDelete');

    Route::resource('avisos', WebAvisoController::class)->except('show');
    Route::patch('avisos/{aviso}/restore', [WebAvisoController::class, 'restore'])->name('avisos.restore');
    Route::delete('avisos/{aviso}/force-delete', [WebAvisoController::class, 'forceDelete'])->name('avisos.forceDelete');

    Route::resource('materiales', WebMaterialController::class)->parameters(['materiales' => 'material'])->except('show');
    Route::patch('materiales/{material}/restore', [WebMaterialController::class, 'restore'])->name('materiales.restore');
    Route::delete('materiales/{material}/force-delete', [WebMaterialController::class, 'forceDelete'])->name('materiales.forceDelete');

    Route::get('/trabajos/{aviso}/pdf', [WebAvisoController::class, 'pdf'])->name('trabajos.pdf');
    Route::get('/trabajos/{aviso}', [WebAvisoController::class, 'show'])->name('trabajos.show');
});
