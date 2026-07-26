<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\SoftDeletes;

class Material extends Model
{
    use SoftDeletes;
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
}
