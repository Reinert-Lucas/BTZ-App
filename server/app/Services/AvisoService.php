<?php
namespace App\Services;

use App\Http\Resources\AvisoResource;
use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Usuario;

class AvisoService
{
    public function getFields()
    {
        return $inputs = [
            [
                'label' => 'Fecha',
                'field' => 'fecha',
                'type' => 'date'
            ],
            [
                'label' => 'Hora',
                'field' => 'hora',
                'type' => 'time'
            ],
            [
                'label' => 'Direccion',
                'field' => 'direccion',
                'type' => 'text'
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
                'type' => 'tel'
            ],
            [
                'label' => 'Mensaje',
                'field' => 'mensaje',
                'type' => 'textarea'
            ],
            [
                'label' => 'Observacion',
                'field' => 'observacion',
                'type' => 'textarea'
            ],
            [
                'label' => 'Urgencia',
                'field' => 'urgencia',
                'type' => 'select',
                'options' => [
                    'urgente' => 'urgente',
                    'media' => 'media',
                    'baja' => 'baja'
                ]
            ],
            [
                'label' => 'Cliente',
                'field' => 'cliente_id',
                'type' => 'select',
                'options' => Cliente::pluck('nombre', 'cliente_id')
            ],
            [
                'label' => 'Operario',
                'field' => 'operario_id',
                'type' => 'select',
                'options' => Usuario::where('rol', 'operario')->pluck('nombre', 'usuario_id')
            ]
        ];
    }
    public function index()
    {
        return Aviso::with(['usuario', 'cliente'])->get();

    }
    public function show(int $aviso)
    {
        return Aviso::findOrFail($aviso);
    }
    public function create(array $avisoValidado)
    {
        return Aviso::create($avisoValidado);
    }
    public function update(array $newAviso, Aviso $aviso)
    {
        $aviso->update($newAviso);
        return $aviso->fresh();
    }
    public function delete(int $aviso)
    {
        return Aviso::find($aviso)->deleteOrFail();
    }
}