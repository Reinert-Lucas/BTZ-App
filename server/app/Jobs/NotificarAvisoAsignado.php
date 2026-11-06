<?php

namespace App\Jobs;

use App\Models\Aviso;
use App\Services\FirebaseNotificationService;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Queue\Queueable;
use Kreait\Firebase\Exception\Messaging\NotFound;

class NotificarAvisoAsignado implements ShouldQueue
{
    use Queueable;

    public int $tries = 3;

    /** @var array<int, int> */
    public array $backoff = [10, 60];

    public function __construct(public int $avisoId) {}

    public function handle(FirebaseNotificationService $firebase): void
    {
        $aviso = Aviso::with('usuario')->find($this->avisoId);
        $usuario = $aviso?->usuario;

        // Se revalida al ejecutar: el aviso pudo cancelarse o reasignarse mientras esperaba en cola.
        if (!$aviso || $aviso->estado !== 'pendiente' || !$usuario?->fcm_token) {
            return;
        }

        try {
            $firebase->sendToToken(
                $usuario->fcm_token,
                '✅ Nuevo Aviso Asignado',
                'Se te ha asignado un nuevo aviso. Toca para ver los detalles.',
                ['aviso_id' => (string) $aviso->aviso_id]
            );
        } catch (NotFound) {
            // Token dado de baja en FCM: se limpia. Cualquier otra excepción se reintenta.
            $usuario->forceFill(['fcm_token' => null])->save();
        }
    }
}
