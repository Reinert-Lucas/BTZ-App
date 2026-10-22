<?php

namespace App\Services;

use Kreait\Firebase\Factory;
use Kreait\Firebase\Messaging\AndroidConfig;
use Kreait\Firebase\Messaging\CloudMessage;
use RuntimeException;

class FirebaseNotificationService
{
    protected $messaging;

    public function sendToToken(
        string $token,
        string $title,
        string $body,
        array $data = []
    ) {
        if (!$this->messaging) {
            $credentials = config('services.firebase.credentials');
            if (!$credentials) {
                throw new RuntimeException('FIREBASE_CREDENTIALS no está configurado.');
            }

            $this->messaging = (new Factory)
                ->withServiceAccount($credentials)
                ->createMessaging();
        }

        $message = CloudMessage::withTarget('token', $token)
            ->withAndroidConfig(AndroidConfig::fromArray([
                'priority' => 'high',
            ]))
            ->withData(array_merge($data, [
                'title' => $title,
                'body' => $body,
            ]));

        return $this->messaging->send($message);
    }
}
