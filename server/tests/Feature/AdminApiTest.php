<?php

use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Material;
use App\Models\Usuario;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use Laravel\Sanctum\Sanctum;

uses(RefreshDatabase::class);

test('only administrators can access administrative API routes', function () {
    $this->getJson('/api/usuarios')->assertUnauthorized();

    Sanctum::actingAs(Usuario::factory()->operario()->create());
    $this->getJson('/api/usuarios')->assertForbidden();
    $this->getJson('/api/clientes')->assertForbidden();
    $this->getJson('/api/materiales')->assertOk();

    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));
    $this->getJson('/api/usuarios')->assertOk();
});

test('an administrator can create update and soft delete a user', function () {
    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));

    $response = $this->postJson('/api/usuarios', [
        'nombre' => 'Nuevo operario',
        'password' => 'password123',
        'rol' => 'operario',
        'dni' => '23456781',
        'telefono' => '+54 11 1234-5678',
    ]);

    $response->assertCreated()->assertJsonPath('data.nombre', 'Nuevo operario');
    $usuarioId = $response->json('data.id');
    $this->assertTrue(Hash::check('password123', Usuario::findOrFail($usuarioId)->password));

    $this->putJson('/api/usuarios/' . $usuarioId, [
        'nombre' => 'Operario actualizado',
        'password' => null,
        'rol' => 'operario',
        'dni' => '23456781',
        'telefono' => '1122334455',
    ])->assertOk()->assertJsonPath('data.nombre', 'Operario actualizado');

    $this->deleteJson('/api/usuarios/' . $usuarioId)->assertOk();
    $this->assertSoftDeleted('usuarios', ['usuario_id' => $usuarioId]);
});

test('an administrator cannot delete their own account', function () {
    $admin = Usuario::factory()->create(['rol' => 'admin']);
    Sanctum::actingAs($admin);

    $this->deleteJson('/api/usuarios/' . $admin->usuario_id)->assertConflict();
    $this->assertDatabaseHas('usuarios', ['usuario_id' => $admin->usuario_id, 'deleted_at' => null]);
});

test('an administrator can create update and soft delete a client', function () {
    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));

    $response = $this->postJson('/api/clientes', [
        'nombre' => 'Cliente de prueba',
        'email' => 'cliente@example.test',
        'asegurado' => false,
        'asegurado_detalle' => 'Este detalle debe descartarse',
    ]);

    $response->assertCreated()
        ->assertJsonPath('data.asegurado', false)
        ->assertJsonPath('data.asegurado_detalle', null);
    $clienteId = $response->json('data.id');

    $this->putJson('/api/clientes/' . $clienteId, [
        'nombre' => 'Cliente actualizado',
        'email' => 'cliente@example.test',
        'asegurado' => true,
        'asegurado_detalle' => 'Cobertura del hogar',
    ])->assertOk()->assertJsonPath('data.asegurado_detalle', 'Cobertura del hogar');

    $this->deleteJson('/api/clientes/' . $clienteId)->assertOk();
    $this->assertSoftDeleted('clientes', ['cliente_id' => $clienteId]);
});

test('an administrator cannot delete a client with pending notices', function () {
    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));
    $cliente = Cliente::factory()->create();
    Aviso::factory()->create(['cliente_id' => $cliente->cliente_id]);

    $this->deleteJson('/api/clientes/' . $cliente->cliente_id)->assertConflict();
    $this->assertDatabaseHas('clientes', ['cliente_id' => $cliente->cliente_id, 'deleted_at' => null]);
});

test('an administrator can create update and soft delete a material', function () {
    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));

    $response = $this->postJson('/api/materiales', [
        'nombre' => 'Material de prueba',
        'detalle' => 'Detalle inicial',
    ]);

    $response->assertCreated()->assertJsonPath('data.nombre', 'Material de prueba');
    $materialId = $response->json('data.id');

    $this->putJson('/api/materiales/' . $materialId, [
        'nombre' => 'Material actualizado',
        'detalle' => 'Detalle actualizado',
    ])->assertOk()->assertJsonPath('data.detalle', 'Detalle actualizado');

    $this->deleteJson('/api/materiales/' . $materialId)->assertOk();
    $this->assertSoftDeleted('materiales', ['material_id' => $materialId]);
});

test('material names must be unique', function () {
    Sanctum::actingAs(Usuario::factory()->create(['rol' => 'admin']));
    Material::factory()->create(['nombre' => 'Nombre repetido']);

    $this->postJson('/api/materiales', [
        'nombre' => 'Nombre repetido',
        'detalle' => 'Detalle',
    ])->assertUnprocessable()
        ->assertJsonValidationErrors('nombre');
});
