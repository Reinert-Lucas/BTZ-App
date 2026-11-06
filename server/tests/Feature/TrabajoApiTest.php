<?php

use App\Models\Aviso;
use App\Models\Material;
use App\Models\Trabajo;
use App\Models\Usuario;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Laravel\Sanctum\Sanctum;

uses(RefreshDatabase::class);

test('an operator only sees their pending assigned work', function () {
    $operario = Usuario::factory()->operario()->create();
    $otroOperario = Usuario::factory()->operario()->create();
    $aviso = Aviso::factory()->create(['usuario_id' => $operario->usuario_id]);
    Aviso::factory()->create(['usuario_id' => $otroOperario->usuario_id]);
    Aviso::factory()->finalizado()->create(['usuario_id' => $operario->usuario_id]);

    Sanctum::actingAs($operario);

    $this->getJson('/api/trabajos')
        ->assertOk()
        ->assertJsonCount(1, 'data')
        ->assertJsonPath('data.0.id', $aviso->aviso_id);
});

test('an operator can submit work and finalize their pending notice', function () {
    $operario = Usuario::factory()->operario()->create();
    $aviso = Aviso::factory()->create(['usuario_id' => $operario->usuario_id]);
    $material = Material::factory()->create();

    Sanctum::actingAs($operario);

    $response = $this->postJson('/api/trabajos', [
        'trabajo_realizado' => 'Se reparó la pérdida de agua',
        'desperfecto' => 'Flexible de conexión dañado',
        'aviso_id' => $aviso->aviso_id,
        'materiales' => [
            ['material_id' => $material->material_id, 'cantidad' => 2],
        ],
    ]);

    $response->assertCreated()
        ->assertJsonPath('status', true)
        ->assertJsonPath('data.aviso_id', $aviso->aviso_id);

    $trabajo = Trabajo::where('aviso_id', $aviso->aviso_id)->firstOrFail();
    $this->assertDatabaseHas('materiales_trabajos', [
        'trabajo_id' => $trabajo->trabajo_id,
        'material_id' => $material->material_id,
        'cantidad' => 2,
    ]);
    $this->assertDatabaseHas('avisos', [
        'aviso_id' => $aviso->aviso_id,
        'estado' => 'finalizado',
    ]);
});

test('an operator cannot submit work for another operators notice', function () {
    $operario = Usuario::factory()->operario()->create();
    $aviso = Aviso::factory()->create();
    $material = Material::factory()->create();

    Sanctum::actingAs($operario);

    $this->postJson('/api/trabajos', [
        'trabajo_realizado' => 'Trabajo realizado',
        'desperfecto' => 'Desperfecto encontrado',
        'aviso_id' => $aviso->aviso_id,
        'materiales' => [
            ['material_id' => $material->material_id, 'cantidad' => 1],
        ],
    ])->assertForbidden();

    $this->assertDatabaseMissing('trabajos', ['aviso_id' => $aviso->aviso_id]);
});

test('an operator cannot submit work for a notice that is no longer pending', function () {
    $operario = Usuario::factory()->operario()->create();
    $aviso = Aviso::factory()->finalizado()->create(['usuario_id' => $operario->usuario_id]);
    $material = Material::factory()->create();

    Sanctum::actingAs($operario);

    $this->postJson('/api/trabajos', [
        'trabajo_realizado' => 'Trabajo realizado',
        'desperfecto' => 'Desperfecto encontrado',
        'aviso_id' => $aviso->aviso_id,
        'materiales' => [
            ['material_id' => $material->material_id, 'cantidad' => 1],
        ],
    ])->assertConflict();

    $this->assertDatabaseMissing('trabajos', ['aviso_id' => $aviso->aviso_id]);
});

test('work submission validates material quantities', function () {
    $operario = Usuario::factory()->operario()->create();
    $aviso = Aviso::factory()->create(['usuario_id' => $operario->usuario_id]);
    $material = Material::factory()->create();

    Sanctum::actingAs($operario);

    $this->postJson('/api/trabajos', [
        'trabajo_realizado' => 'Trabajo realizado',
        'desperfecto' => 'Desperfecto encontrado',
        'aviso_id' => $aviso->aviso_id,
        'materiales' => [
            ['material_id' => $material->material_id, 'cantidad' => 0],
        ],
    ])->assertUnprocessable()
        ->assertJsonValidationErrors('materiales.0.cantidad');
});

test('an operator cannot use the finalized work endpoint to view another operators notices', function () {
    $operario = Usuario::factory()->operario()->create();
    $otroOperario = Usuario::factory()->operario()->create();
    $propio = Aviso::factory()->finalizado()->create(['usuario_id' => $operario->usuario_id]);
    Aviso::factory()->finalizado()->create(['usuario_id' => $otroOperario->usuario_id]);

    Sanctum::actingAs($operario);

    $this->getJson('/api/trabajos/finalizados/' . $otroOperario->usuario_id)
        ->assertOk()
        ->assertJsonCount(1, 'data')
        ->assertJsonPath('data.0.id', $propio->aviso_id);
});
