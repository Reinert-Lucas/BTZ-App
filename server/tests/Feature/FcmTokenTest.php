<?php

/* use App\Models\Usuario;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;

uses(RefreshDatabase::class);

test('an authenticated user can save their FCM token', function () {
    $usuario = Usuario::factory()->operario()->create();
    Sanctum::actingAs($usuario);

    $response = $this->postJson('/api/fcm-token', [
        'token' => 'test-fcm-token',
    ]);

    $response->assertOk();
    $this->assertDatabaseHas('usuarios', [
        'usuario_id' => $usuario->usuario_id,
        'fcm_token' => 'test-fcm-token',
    ]);
}); */