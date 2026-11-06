<?php

use App\Models\Usuario;
use Illuminate\Foundation\Testing\RefreshDatabase;

uses(RefreshDatabase::class);

test('a user can log in and receive a bearer token', function () {
    $usuario = Usuario::factory()->create([
        'dni' => '12345678',
        'password' => 'password123',
    ]);

    $this->postJson('/api/login', [
        'dni' => '12345678',
        'password' => 'password123',
    ])->assertOk()
        ->assertJsonPath('status', true)
        ->assertJsonPath('user.id', $usuario->usuario_id)
        ->assertJsonStructure(['token']);

    $this->assertDatabaseCount('personal_access_tokens', 1);
});

test('invalid login credentials are rejected', function () {
    Usuario::factory()->create([
        'dni' => '12345678',
        'password' => 'password123',
    ]);

    $this->postJson('/api/login', [
        'dni' => '12345678',
        'password' => 'incorrect-password',
    ])->assertUnauthorized()
        ->assertJsonPath('status', false);
});

test('protected API routes reject unauthenticated requests', function () {
    $this->getJson('/api/me')->assertUnauthorized();
});

test('a bearer token can access the current user endpoint', function () {
    $usuario = Usuario::factory()->create();
    $token = $usuario->createToken('test')->plainTextToken;

    $this->withToken($token)
        ->getJson('/api/me')
        ->assertOk()
        ->assertJsonPath('user.id', $usuario->usuario_id);
});

test('logout revokes the bearer token and clears the FCM token', function () {
    $usuario = Usuario::factory()->create();
    $usuario->forceFill(['fcm_token' => 'device-token'])->save();
    $token = $usuario->createToken('test')->plainTextToken;

    $this->withToken($token)
        ->postJson('/api/logout')
        ->assertOk()
        ->assertJsonPath('status', true);

    $this->assertDatabaseCount('personal_access_tokens', 0);
    $this->assertDatabaseHas('usuarios', [
        'usuario_id' => $usuario->usuario_id,
        'fcm_token' => null,
    ]);
});
