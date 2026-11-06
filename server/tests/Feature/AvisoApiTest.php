<?php

use App\Jobs\NotificarAvisoAsignado;
use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Trabajo;
use App\Models\Usuario;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Queue;
use Laravel\Sanctum\Sanctum;

uses(RefreshDatabase::class);

test('operators only see their own notices while administrators see all notices', function () {
    $operario = Usuario::factory()->operario()->create();
    $otroOperario = Usuario::factory()->operario()->create();
    $aviso = Aviso::factory()->create(['usuario_id' => $operario->usuario_id]);
    Aviso::factory()->create(['usuario_id' => $otroOperario->usuario_id]);

    Sanctum::actingAs($operario);
    $this->getJson('/api/avisos')
        ->assertOk()
        ->assertJsonCount(1, 'data')
        ->assertJsonPath('data.0.id', $aviso->aviso_id);

    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));
    $this->getJson('/api/avisos')->assertOk()->assertJsonCount(2, 'data');
});

test('creating a notice defaults it to pending and queues an assignment notification', function () {
    Queue::fake();
    $admin = Usuario::factory()->create(['rol' => 'admin']);
    $operario = Usuario::factory()->operario()->create();
    $cliente = Cliente::factory()->create();
    Sanctum::actingAs($admin);

    $response = $this->postJson('/api/avisos', [
        'fecha' => '2026-11-06',
        'hora' => '10:30',
        'direccion' => 'Calle de prueba 123',
        'telefono' => '1122334455',
        'urgencia' => 'media',
        'usuario_id' => $operario->usuario_id,
        'cliente_id' => $cliente->cliente_id,
    ]);

    $response->assertCreated()->assertJsonPath('data.estado', 'pendiente');
    Queue::assertPushed(NotificarAvisoAsignado::class);
    $this->assertDatabaseHas('avisos', [
        'aviso_id' => $response->json('data.id'),
        'estado' => 'pendiente',
    ]);
});

test('a notice without a submitted job cannot be marked finalized', function () {
    $admin = Usuario::factory()->create(['rol' => 'admin']);
    $aviso = Aviso::factory()->create();
    Sanctum::actingAs($admin);

    $this->putJson('/api/avisos/' . $aviso->aviso_id, [
        'fecha' => substr((string) $aviso->fecha, 0, 10),
        'hora' => $aviso->hora,
        'direccion' => $aviso->direccion,
        'telefono' => $aviso->telefono,
        'mensaje' => $aviso->mensaje,
        'observacion' => $aviso->observacion,
        'estado' => 'finalizado',
        'urgencia' => $aviso->urgencia,
        'usuario_id' => $aviso->usuario_id,
        'cliente_id' => $aviso->cliente_id,
    ])->assertConflict();

    $this->assertDatabaseHas('avisos', [
        'aviso_id' => $aviso->aviso_id,
        'estado' => 'pendiente',
    ]);
});

test('a notice with a submitted job cannot be deleted', function () {
    $admin = Usuario::factory()->create(['rol' => 'admin']);
    $aviso = Aviso::factory()->finalizado()->create();
    Trabajo::factory()->create(['aviso_id' => $aviso->aviso_id]);
    Sanctum::actingAs($admin);

    $this->deleteJson('/api/avisos/' . $aviso->aviso_id)->assertConflict();
    $this->assertDatabaseHas('avisos', ['aviso_id' => $aviso->aviso_id, 'deleted_at' => null]);
});

test('notice creation validates the assigned operator and client', function () {
    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));

    $this->postJson('/api/avisos', [
        'fecha' => '2026-11-06',
        'hora' => '10:30',
        'direccion' => 'Calle de prueba 123',
        'telefono' => '1122334455',
        'urgencia' => 'media',
        'usuario_id' => 99999,
        'cliente_id' => 99999,
    ])->assertUnprocessable()
        ->assertJsonValidationErrors(['usuario_id', 'cliente_id']);
});
