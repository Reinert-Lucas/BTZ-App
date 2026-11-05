<?php

use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\DB;

uses(RefreshDatabase::class);

test('the database seeder creates records with valid foreign keys', function () {
    $this->seed();

    $this->assertDatabaseCount('materiales', 12);
    $this->assertDatabaseCount('avisos', 15);
    $this->assertDatabaseCount('trabajos', 5);
    $this->assertSame([], DB::select('PRAGMA foreign_key_check'));
});
