<?php

use Illuminate\Foundation\Testing\RefreshDatabase;

uses(RefreshDatabase::class);

test('the database seeder creates records with valid foreign keys', function () {
    $this->seed();

    $this->assertDatabaseCount('materiales', 12);
    $this->assertDatabaseCount('avisos', 15);
    $this->assertDatabaseCount('trabajos', 5);
});
