<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Notifications\Notifiable;

class Aviso extends Model
{
    use HasFactory, Notifiable;
    protected $guarded = [];
    protected $table = 'avisos';
    protected $primaryKey = 'aviso_id';

    public function usuario(): BelongsTo
    {
        return $this->belongsTo(Usuario::class, 'usuario_id');
    }
    public function cliente(): BelongsTo
    {
        return $this->belongsTo(Cliente::class, 'cliente_id');
    }
    public function trabajo(): HasOne
    {
        return $this->hasOne(Trabajo::class, 'aviso_id');
    }
}
