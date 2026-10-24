<?php

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

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__ . '/../routes/web.php',
        api: __DIR__ . '/../routes/api.php',
        commands: __DIR__ . '/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        $middleware->alias([
            'isAdmin' => \App\Http\Middleware\isAdmin::class,
        ]);
    })
    ->withExceptions(function (Exceptions $exceptions): void {
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

        $exceptions->render(function (HttpExceptionInterface $e, Request $request) {
            if ($request->is('api/*')) {
                // No se filtran mensajes internos del framework (405, etc.).
                $message = match (true) {
                    $e instanceof NotFoundHttpException => 'Recurso no encontrado',
                    $e instanceof MethodNotAllowedHttpException => 'Método no permitido',
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
        });

        $exceptions->render(function (QueryException $e, Request $request) {
            if (!$request->is('api/*')) {
                return;
            }

            [$status, $message] = match ($e->errorInfo[1] ?? null) {
                1062 => [409, 'Ya existe un registro con esos datos'],
                1451, 1452 => [409, 'La operación entra en conflicto con registros relacionados'],
                default => [500, 'Error al procesar la solicitud'],
            };

            return response()->json(['status' => false, 'message' => $message], $status);
        });
    })->create();