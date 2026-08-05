<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class isAdmin
{
    /**
     * Handle an incoming request.
     *
     * @param  Closure(Request): (Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        // Revisa que haya una sesion
        $user = $request->user();
        // Revisa Rol, Diferencia entre peticion de API y WEB
        if (!$user || $user->rol !== 'admin') {
            if ($request->is('api/*') || $request->expectsJson()) {
                return response()->json([
                    'status' => false,
                    'message' => $user ? 'Acceso denegado' : 'No autenticado',
                ], $user ? 403 : 401);
            }
            return redirect()->route('admin.noaccess');
        }
        return $next($request);
    }
}
