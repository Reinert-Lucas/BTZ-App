<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class Trabajo extends Model
{
    protected $guarded = [];
    protected $table = 'trabajos';
    protected $primaryKey = 'trabajo_id';

    public function materiales(): BelongsToMany
    {
        return $this->belongsToMany(
            Material::class,
            'materiales_trabajos',
            'trabajo_id',
            'material_id'
        )->withPivot('cantidad');
    }
    public function aviso(): BelongsTo
    {
        return $this->belongsTo(Aviso::class, 'aviso_id');
    }
}
