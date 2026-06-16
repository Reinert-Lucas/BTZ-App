<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Notifications\Notifiable;

class Aviso extends Model
{
    use HasFactory, Notifiable;
    protected $guarded = [];
    protected $table = 'avisos';
    protected $primaryKey = 'aviso_id';

    public function detalle_aviso(): BelongsTo
    {
        return $this->belongsTo(DetalleAviso::class);
    }
}
