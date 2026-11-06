<?php

use App\Models\Usuario;
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
});

test('saving an FCM token removes it from any previous user', function () {
    $usuarioAnterior = Usuario::factory()->operario()->create();
    $usuarioActual = Usuario::factory()->operario()->create();
    $usuarioAnterior->forceFill(['fcm_token' => 'shared-device-token'])->save();
    Sanctum::actingAs($usuarioActual);

    $this->postJson('/api/fcm-token', ['token' => 'shared-device-token'])->assertOk();

    $this->assertDatabaseHas('usuarios', [
        'usuario_id' => $usuarioAnterior->usuario_id,
        'fcm_token' => null,
    ]);
    $this->assertDatabaseHas('usuarios', [
        'usuario_id' => $usuarioActual->usuario_id,
        'fcm_token' => 'shared-device-token',
    ]);
});

test('an FCM token is required', function () {
    Sanctum::actingAs(Usuario::factory()->operario()->create());

    $this->postJson('/api/fcm-token', [])->assertUnprocessable()
        ->assertJsonValidationErrors('token');
});
