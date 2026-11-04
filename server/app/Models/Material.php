<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Prunable;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\SoftDeletes;
use Illuminate\Notifications\Notifiable;

class Material extends Model
{
    use SoftDeletes, HasFactory, Notifiable, Prunable;
    protected $table = 'materiales';
    protected $primaryKey = 'material_id';
    protected $fillable = ['nombre', 'detalle'];
    public function trabajos(): BelongsToMany
    {
        return $this->belongsToMany(
            Trabajo::class,
            'materiales_trabajos',
            'material_id',
            'trabajo_id'
        )->withPivot('cantidad');
    }
    public function prunable(): Builder
    {
        return static::onlyTrashed()
            ->where('deleted_at', '<=', now()->subDays(90))
            ->whereDoesntHave('trabajos', fn($q) => $q->withTrashed());
    }
}
