<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Material extends Model
{
    protected $guarded = [];
    protected $table = 'materiales';
    protected $primaryKey = 'material_id';

    public function trabajos()
    {
        return $this->belongsToMany(
            Trabajo::class,
            'materiales_trabajos',
            'material_id',
            'trabajo_id'
        )->withPivot('cantidad');
    }
}
