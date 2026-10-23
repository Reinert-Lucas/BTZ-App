<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Casts\Attribute;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Support\Facades\Hash;
use Laravel\Sanctum\HasApiTokens;

class Usuario extends Authenticatable
{
    use HasApiTokens, HasFactory, SoftDeletes;
    protected $table = 'usuarios';
    protected $primaryKey = 'usuario_id';
    protected $fillable = ['nombre', 'password', 'dni', 'telefono', 'rol'];
    protected $hidden = ['password', 'fcm_token'];
    protected function casts(): array
    {
        return ['password' => 'hashed'];
    }
    protected function password(): Attribute
    {
        return Attribute::make(
            set: fn(string $value) => Hash::make($value),
        );
    }
    public function aviso(): HasMany
    {
        return $this->hasMany(Aviso::class, 'usuario_id');
    }
}
