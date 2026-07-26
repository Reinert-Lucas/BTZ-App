<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;
use Illuminate\Database\Eloquent\SoftDeletes;

class Trabajo extends Model
{
    use SoftDeletes;
    protected $table = 'trabajos';
    protected $primaryKey = 'trabajo_id';

    protected $fillable = ['trabajo_realizado', 'desperfecto', 'aviso_id'];

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
