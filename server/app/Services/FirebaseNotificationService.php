<?php

namespace App\Services;

use Kreait\Firebase\Contract\Messaging;
use Kreait\Firebase\Factory;
use Kreait\Firebase\Messaging\AndroidConfig;
use Kreait\Firebase\Messaging\CloudMessage;
use RuntimeException;

class FirebaseNotificationService
{
    private ?Messaging $messaging = null;

    /**
     * @param  array<string, string>  $data
     * @return array<string, mixed>  Respuesta de FCM 
     */
    public function sendToToken(
        string $token,
        string $title,
        string $body,
        array $data = []
    ): array {
        $message = CloudMessage::withTarget('token', $token)
            ->withAndroidConfig(AndroidConfig::fromArray(['priority' => 'high']))
            ->withData(array_merge($data, [
                'title' => $title,
                'body' => $body,
            ]));

        return $this->messaging()->send($message);
    }

    private function messaging(): Messaging
    {
        if ($this->messaging !== null) {
            return $this->messaging;
        }

        $credenciales = config('services.firebase.credentials');
        if (! $credenciales) {
            throw new RuntimeException('FIREBASE_CREDENTIALS no está configurado.');
        }

        // Una ruta relativa se resuelve contra la raíz del proyecto, no contra el cwd del worker.
        $ruta = is_file($credenciales) ? $credenciales : base_path($credenciales);

        return $this->messaging = (new Factory)
            ->withServiceAccount($ruta)
            ->createMessaging();
    }
}
