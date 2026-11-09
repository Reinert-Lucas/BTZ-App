<?php

namespace App\Providers;

use App\Services\FirebaseNotificationService;
use Illuminate\Cache\RateLimiting\Limit;
use Illuminate\Http\Request;
use Illuminate\Pagination\Paginator;
use Illuminate\Support\Facades\RateLimiter;
use Illuminate\Support\Facades\Route;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton(FirebaseNotificationService::class);
    }

    public function boot(): void
    {
        Paginator::useBootstrapFour();

        // IDs no numéricos => 404 limpio (en vez de TypeError/500).
        foreach (['usuario', 'cliente', 'material', 'aviso', 'usuario_id'] as $parametro) {
            Route::pattern($parametro, '[0-9]+');
        }

        RateLimiter::for('login', function (Request $request) {
            // input() puede ser un array (dni[]=x): no se castea a string sin comprobar.
            $dni = $request->input('dni');
            $dni = is_string($dni) ? mb_strtolower($dni) : '';

            return [
                Limit::perMinute(20)->by($request->ip()),
                Limit::perMinute(5)->by($request->ip() . '|' . $dni),
            ];
        });

        // Límite general de la API: por usuario autenticado o, si no hay, por IP.
        RateLimiter::for('api', fn(Request $request) => Limit::perMinute(60)
            ->by($request->user()?->getAuthIdentifier() ?: $request->ip()));
    }
}
