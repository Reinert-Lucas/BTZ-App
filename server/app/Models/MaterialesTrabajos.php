<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Notifications\Notifiable;

class MaterialesTrabajos extends Model
{
    use HasFactory, Notifiable;
    protected $table = 'materiales_trabajos';
    protected $primaryKey = 'materiales_trabajos_id';

    protected $fillable = ['material_id', 'trabajo_id', 'cantidad'];

    public function material(): BelongsTo
    {
        return $this->belongsTo(Material::class, 'material_id');
    }

    public function trabajo(): BelongsTo
    {
        return $this->belongsTo(Trabajo::class, 'trabajo_id');
    }
}
