<?php

namespace App\Providers;

use App\Services\FirebaseNotificationService;
use Illuminate\Cache\RateLimiting\Limit;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Http\Middleware\TrustProxies;
use Illuminate\Http\Request;
use Illuminate\Pagination\Paginator;
use Illuminate\Support\Facades\RateLimiter;
use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\URL;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton(FirebaseNotificationService::class);
    }

    public function boot(): void
    {
        $esProduccion = $this->app->environment('production');

        Paginator::useBootstrapFour();

        // En desarrollo avisa de N+1 (lanza excepción); en producción no hace nada.
        Model::preventLazyLoading(! $esProduccion);

        if ($esProduccion) {
            URL::forceScheme('https');
        }

        // Proxy inverso (Nginx/Cloudflare). TRUSTED_PROXIES="10.0.0.0/8,172.16.0.0/12" o "*"
        // (usar "*" solo si el servidor NO es accesible directamente).
        $proxies = config('app.trusted_proxies');
        if (is_string($proxies) && $proxies !== '') {
            TrustProxies::at($proxies === '*' ? '*' : array_map('trim', explode(',', $proxies)));
        }

        // IDs no numéricos => 404 limpio (en vez de TypeError/500).
        foreach (['usuario', 'cliente', 'material', 'aviso', 'usuario_id'] as $parametro) {
            Route::pattern($parametro, '[0-9]+');
        }

        RateLimiter::for('login', function (Request $request) {
            // dni[]=x no debe provocar "Array to string conversion" antes de validar.
            $dni = $request->input('dni');
            $dni = is_string($dni) ? mb_strtolower($dni) : '';

            return [
                Limit::perMinute(20)->by($request->ip()),
                Limit::perMinute(5)->by('dni|' . $dni),
            ];
        });

        // Límite general de la API: por usuario autenticado o, si no hay, por IP.
        RateLimiter::for('api', fn(Request $request) => Limit::perMinute(60)
            ->by($request->user()?->getAuthIdentifier() ?: $request->ip()));
    }
}
