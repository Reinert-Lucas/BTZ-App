<?php
namespace App\Services;

use App\Http\Resources\AvisoResource;
use App\Models\Aviso;
use App\Models\Cliente;
use App\Models\Usuario;
use Illuminate\Http\Request;

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
    public function index(Request $request)
    {
        return Aviso::query()
            ->when($request->aviso_id, function ($query, $aviso_id) {
                $query->where('aviso_id', $aviso_id);
            })
            ->when($request->fecha, function ($query, $fecha) {
                $query->where('fecha', $fecha);
            })
            ->when($request->hora, function ($query, $hora) {
                $query->where('hora', $hora);
            })
            ->when($request->direccion, function ($query, $direccion) {
                $query->where('direccion', 'like', "%{$direccion}%");
            })
            ->when($request->telefono, function ($query, $telefono) {
                $query->where('telefono', $telefono);
            })
            ->when($request->estado, function ($query, $estado) {
                $query->where('estado', $estado);
            })
            ->paginate(10)
            ->withQueryString();
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
        return Aviso::findOrFail($aviso)->delete();
    }
}