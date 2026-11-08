<?php

namespace App\Jobs;

use App\Models\Aviso;
use App\Services\FirebaseNotificationService;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Queue\Queueable;
use Illuminate\Support\Facades\Log;
use Kreait\Firebase\Exception\Messaging\NotFound;
use Throwable;

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

        if (!$aviso) {
            Log::warning('Notificación FCM omitida: aviso inexistente.', [
                'aviso_id' => $this->avisoId,
            ]);
            return;
        }

        $contexto = [
            'aviso_id' => $aviso->aviso_id,
            'usuario_id' => $aviso->usuario_id,
        ];

        // Se revalida al ejecutar: el aviso pudo cancelarse o reasignarse mientras esperaba en cola.
        if ($aviso->estado !== 'pendiente') {
            Log::info('Notificación FCM omitida: el aviso no está pendiente.', $contexto + [
                'estado' => $aviso->estado,
            ]);
            return;
        }

        if (!$usuario?->fcm_token) {
            Log::warning('Notificación FCM omitida: el usuario no tiene token registrado.', $contexto);
            return;
        }

        Log::info('Enviando notificación FCM.', $contexto + [
            'intento' => $this->attempts(),
        ]);

        try {
            $messageId = $firebase->sendToToken(
                $usuario->fcm_token,
                '✅ Nuevo Aviso Asignado',
                'Se te ha asignado un nuevo aviso. Toca para ver los detalles.',
                ['aviso_id' => (string) $aviso->aviso_id]
            );
            Log::info('Notificación FCM enviada.', $contexto + [
                'fcm_message_id' => is_string($messageId) ? $messageId : null,
            ]);
        } catch (NotFound) {
            // Token dado de baja en FCM: se limpia. Cualquier otra excepción se reintenta.
            $usuario->forceFill(['fcm_token' => null])->save();
            Log::warning('Token FCM inválido; se eliminó del usuario.', $contexto);
        } catch (Throwable $exception) {
            Log::error('Error al enviar notificación FCM.', $contexto + [
                'intento' => $this->attempts(),
                'exception' => $exception::class,
                'error' => $exception->getMessage(),
            ]);

            throw $exception;
        }
    }
}
