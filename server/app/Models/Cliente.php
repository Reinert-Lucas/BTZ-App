<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Casts\Attribute;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Prunable;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Notifications\Notifiable;

class Cliente extends Model
{
    use HasFactory, Notifiable, SoftDeletes, Prunable;

    protected $table = 'clientes';
    protected $primaryKey = 'cliente_id';
    protected $fillable = ['nombre', 'email', 'asegurado', 'asegurado_detalle'];

    public function aviso(): HasMany
    {
        return $this->hasMany(Aviso::class, 'cliente_id');
    }

    protected function casts(): array
    {
        return ['asegurado' => 'boolean'];
    }

    protected function aseguradoTexto(): Attribute
    {
        return Attribute::get(fn() => $this->asegurado ? 'SI' : 'NO');
    }

    protected function aseguradoDetalleTexto(): Attribute
    {
        return Attribute::get(fn() => $this->asegurado_detalle ?? '-');
    }

    /** Despues de 90 dias eliminado, se purga. Solo si no tiene avisos */
    public function prunable(): Builder
    {
        return static::onlyTrashed()
            ->where('deleted_at', '<=', now()->subDays(90))
            ->whereDoesntHave('aviso', fn($q) => $q->withTrashed());
    }
}
