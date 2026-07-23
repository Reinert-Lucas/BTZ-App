<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Notifications\Notifiable;

class Cliente extends Model
{
    use HasFactory, Notifiable;
    protected $guarded = [];
    protected $table = 'clientes';
    protected $primaryKey = 'cliente_id';
    public function aviso(): HasOne
    {
        return $this->hasOne(Aviso::class, 'cliente_id');
    }
}
