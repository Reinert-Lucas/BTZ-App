<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Notifications\Notifiable;

class Cliente extends Model
{
    use HasFactory, Notifiable, SoftDeletes;
    protected $table = 'clientes';
    protected $primaryKey = 'cliente_id';
    protected $fillable = ['nombre', 'email', 'asegurado', 'asegurado_detalle'];
    public function aviso(): HasOne
    {
        return $this->hasOne(Aviso::class, 'cliente_id');
    }
}
