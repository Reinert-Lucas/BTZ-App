<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class MaterialesTrabajos extends Model
{
    protected $guarded = [];
    protected $table = 'materiales_trabajos';
    protected $primaryKey = 'materiales_trabajos_id';

    public function material()
    {
        return $this->belongsTo(Material::class, 'material_id');
    }

    public function trabajo()
    {
        return $this->belongsTo(Trabajo::class, 'trabajo_id');
    }
}
