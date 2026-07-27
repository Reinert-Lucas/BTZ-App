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
        if ($request->user()) {
            if ($request->user()->rol !== 'admin') {
                return response()->json([
                    'message' => 'Acceso denegado',
                    'status' => false
                ], 403);
            }
        }
        return $next($request);
    }
}
