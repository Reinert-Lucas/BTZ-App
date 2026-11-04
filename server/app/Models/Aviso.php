<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Prunable;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasOne;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Notifications\Notifiable;

class Aviso extends Model
{
    use HasFactory, Notifiable, SoftDeletes, Prunable;
    protected $table = 'avisos';
    protected $primaryKey = 'aviso_id';

    protected $fillable = ['fecha', 'hora', 'direccion', 'telefono', 'mensaje', 'observacion', 'estado', 'urgencia', 'usuario_id', 'cliente_id'];

    public function usuario(): BelongsTo
    {
        return $this->belongsTo(Usuario::class, 'usuario_id')->withTrashed();
    }
    public function cliente(): BelongsTo
    {
        return $this->belongsTo(Cliente::class, 'cliente_id')->withTrashed();
    }
    public function trabajo(): HasOne
    {
        return $this->hasOne(Trabajo::class, 'aviso_id');
    }
    public function prunable(): Builder
    {
        return static::onlyTrashed()
            ->where('deleted_at', '<=', now()->subDays(90))
            ->whereDoesntHave('trabajo', fn($q) => $q->withTrashed());
    }
}
