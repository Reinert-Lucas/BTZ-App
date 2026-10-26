<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Laravel\Sanctum\HasApiTokens;

class Usuario extends Authenticatable
{
    use HasApiTokens, HasFactory, SoftDeletes;

    protected $table = 'usuarios';
    protected $primaryKey = 'usuario_id';
    protected $fillable = ['nombre', 'password', 'dni', 'telefono', 'rol'];
    protected $hidden = ['password', 'fcm_token'];

    // Única fuente de hasheo: el cast 'hashed' (no vuelve a hashear un valor ya hasheado).
    protected function casts(): array
    {
        return ['password' => 'hashed'];
    }

    public function aviso(): HasMany
    {
        return $this->hasMany(Aviso::class, 'usuario_id');
    }
}