<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Notifications\Notifiable;

class DetalleAviso extends Model
{
    use HasFactory, Notifiable;
    protected $guarded = [];
    protected $table = 'detalle_avisos';
    protected $primaryKey = 'detalle_aviso_id';
}
