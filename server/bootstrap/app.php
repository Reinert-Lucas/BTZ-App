<?php

use App\Http\Middleware\isAdmin;
use Illuminate\Auth\AuthenticationException;
use Illuminate\Database\QueryException;
use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Request;
use Illuminate\Validation\ValidationException;
use Symfony\Component\HttpKernel\Exception\HttpExceptionInterface;
use Symfony\Component\HttpKernel\Exception\MethodNotAllowedHttpException;
use Symfony\Component\HttpKernel\Exception\NotFoundHttpException;
use Symfony\Component\HttpKernel\Exception\TooManyRequestsHttpException;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__ . '/../routes/web.php',
        api: __DIR__ . '/../routes/api.php',
        commands: __DIR__ . '/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        $middleware->alias([
            'isAdmin' => isAdmin::class,
        ]);

        // Rate limit general de la API (el limiter 'api' se define en AppServiceProvider).
        $middleware->throttleApi();

        // SI HAY proxy/balanceador delante (Nginx, Cloudflare, etc.) ACTIVAR ESTO:
        // sin él, $request->ip() es la IP del proxy y todos los usuarios comparten el
        // mismo límite de login (20/min). Usar las IPs reales del proxy; '*' solo si el
        // servidor NO es accesible directamente (si no, X-Forwarded-For se puede falsificar).
        // $middleware->trustProxies(at: ['10.0.0.0/8']);
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        // Nunca se reinyectan contraseñas en la sesión (sesiones en BD, sin cifrar por defecto).
        $inputSeguro = fn(Request $request): array => $request->except([
            'password',
            'password_confirmation',
            'current_password',
        ]);

        // Toda ruta api/* responde JSON, incluso ante errores no controlados.
        $exceptions->shouldRenderJsonWhen(
            fn(Request $request, Throwable $e) => $request->is('api/*') || $request->expectsJson()
        );

        $exceptions->render(function (AuthenticationException $e, Request $request) {
            if ($request->is('api/*')) {
                return response()->json(['status' => false, 'message' => 'No autenticado'], 401);
            }
        });

        $exceptions->render(function (ValidationException $e, Request $request) {
            if ($request->is('api/*')) {
                return response()->json([
                    'status' => false,
                    'message' => 'Datos inválidos',
                    'errors' => $e->errors(),
                ], 422);
            }
        });

        $exceptions->render(function (HttpExceptionInterface $e, Request $request) use ($inputSeguro) {
            if ($request->is('api/*')) {
                // No se filtran mensajes internos del framework.
                $message = match (true) {
                    $e instanceof NotFoundHttpException => 'Recurso no encontrado',
                    $e instanceof MethodNotAllowedHttpException => 'Método no permitido',
                    $e instanceof TooManyRequestsHttpException => 'Demasiados intentos. Intenta nuevamente en unos instantes.',
                    default => $e->getMessage() ?: 'Error en la solicitud',
                };

                return response()->json(
                    ['status' => false, 'message' => $message],
                    $e->getStatusCode(),
                    $e->getHeaders()
                );
            }

            // Web: sesión/CSRF vencido (419) => vuelve al login con un mensaje.
            if ($e->getStatusCode() === 419) {
                return redirect()->route('login')
                    ->withErrors(['dni' => 'Tu sesión expiró. Vuelve a iniciar sesión.']);
            }

            // Web: conflictos de negocio (abort_if(..., 409)) vuelven al formulario con un mensaje.
            if ($e->getStatusCode() === 409) {
                return back()
                    ->withInput($inputSeguro($request))
                    ->with('error', $e->getMessage() ?: 'La operación no se pudo completar.');
            }
        });

        $exceptions->render(function (QueryException $e, Request $request) use ($inputSeguro) {
            // Códigos de error de MySQL/MariaDB (el proyecto usa MySQL en producción).
            [$status, $message] = match ($e->errorInfo[1] ?? null) {
                1062 => [409, 'Ya existe un registro con esos datos'],
                1451, 1452 => [409, 'La operación entra en conflicto con registros relacionados'],
                default => [500, 'Error al procesar la solicitud'],
            };

            if ($request->is('api/*')) {
                return response()->json(['status' => false, 'message' => $message], $status);
            }

            // Web: solo los conflictos vuelven al formulario; el resto usa la página 500 por defecto.
            if ($status === 409) {
                return back()->withInput($inputSeguro($request))->with('error', $message);
            }
        });

        // Último recurso para la API: cualquier otro Throwable (TypeError, error de
        // Firebase, etc.) mantiene el mismo formato {status, message} que espera la app
        // Kotlin. En desarrollo (APP_DEBUG=true) se deja el detalle por defecto de Laravel.
        $exceptions->render(function (Throwable $e, Request $request) {
            if ($request->is('api/*') && !config('app.debug')) {
                return response()->json([
                    'status' => false,
                    'message' => 'Error interno del servidor',
                ], 500);
            }
        });
    })->create();