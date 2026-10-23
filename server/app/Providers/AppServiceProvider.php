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
    /**
     * Register any application services.
     */
    public function register(): void
    {
        $this->app->singleton(FirebaseNotificationService::class);
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        Paginator::useBootstrapFour();
        // IDs no numéricos => 404 limpio (en vez de TypeError/500).
        foreach (['usuario', 'cliente', 'material', 'aviso', 'usuario_id'] as $parametro) {
            Route::pattern($parametro, '[0-9]+');
        }

        RateLimiter::for('login', fn(Request $request) => [
            Limit::perMinute(20)->by($request->ip()),
            Limit::perMinute(5)->by('dni|' . mb_strtolower((string) $request->input('dni'))),
        ]);
    }
}
