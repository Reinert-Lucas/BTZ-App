<?php

use App\Models\Usuario;
use Illuminate\Foundation\Testing\RefreshDatabase;

uses(RefreshDatabase::class);

test('administrative pages require a web session', function () {
    $this->get('/admin')->assertRedirect(route('login'));
});

test('an operator is redirected away from the administration panel', function () {
    $this->actingAs(Usuario::factory()->operario()->create())
        ->get('/admin')
        ->assertRedirect(route('admin.noaccess'));
});

test('an administrator can open the dashboard', function () {
    $this->actingAs(Usuario::factory()->create(['rol' => 'admin']))
        ->get('/admin')
        ->assertOk();
});

test('an administrator can log in through the web form', function () {
    Usuario::factory()->create([
        'rol' => 'admin',
        'dni' => '12345678',
        'password' => 'password123',
    ]);

    $this->post('/login', [
        'dni' => '12345678',
        'password' => 'password123',
    ])->assertRedirect(route('admin.dashboard'));
});

test('invalid web login credentials return to the login form', function () {
    $this->post('/login', [
        'dni' => '12345678',
        'password' => 'incorrect-password',
    ])->assertRedirect()
        ->assertSessionHasErrors('dni');
});

test('web logout ends the session and redirects to the login page', function () {
    $this->actingAs(Usuario::factory()->create(['rol' => 'admin']))
        ->post('/logout')
        ->assertRedirect(route('login'));
});
