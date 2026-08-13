<?php

namespace App\Http\Controllers;

use App\Http\Requests\AvisoRequest;
use App\Http\Resources\AvisoResource;
use App\Http\Resources\TrabajoResource;
use App\Models\Aviso;
use App\Models\Trabajo;
use App\Services\AvisoService;
use Illuminate\Http\Request;

class WebAvisoController extends Controller
{
    private AvisoService $service;
    public function __construct(AvisoService $service)
    {
        $this->service = $service;
    }
    public function index(Request $request)
    {
        $avisos = $this->service->index($request);
        $columns = [
            [
                'label' => 'Nro',
                'field' => 'aviso_id'
            ],
            [
                'label' => 'Fecha',
                'field' => 'fecha',
            ],
            [
                'label' => 'Hora',
                'field' => 'hora',
            ],
            [
                'label' => 'Direccion',
                'field' => 'direccion',
            ],
            [
                'label' => 'Telefono',
                'field' => 'telefono',
            ],
            [
                'label' => 'Mensaje',
                'field' => 'mensaje',
            ],
            [
                'label' => 'Observacion',
                'field' => 'observacion',
            ],
            [
                'label' => 'Estado',
                'field' => 'estado',
            ],
            [
                'label' => 'Urgencia',
                'field' => 'urgencia',
            ]
        ];
        $filters = [
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
                'label' => 'Estado',
                'field' => 'estado',
                'type' => 'select',
                'options' => [
                    'pendiente' => 'Pendiente',
                    'finalizado' => 'Finalizado',
                    'cancelado' => 'Cancelado'
                ]
            ],
            [
                'label' => 'Urgencia',
                'field' => 'urgencia',
                'type' => 'select',
                'options' => [
                    'urgente' => 'Urgente',
                    'media' => 'Media',
                    'baja' => 'Alta'
                ]
            ]
        ];
        return view('admin.avisos.index', [
            'avisos' => AvisoResource::collection($avisos),
            'columns' => $columns,
            'filtros' => $filters
        ]);
    }
    public function show(int $aviso)
    {
        $trabajo = Trabajo::with('materiales', 'aviso.usuario', 'aviso.cliente')->where('aviso_id', $aviso)->firstOrFail();
        return view('admin.trabajo', [
            'trabajo' => $trabajo,
        ]);
    }
    public function create()
    {
        return view('admin.avisos.create', ['inputs' => $this->service->getFields()]);
    }
    public function store(AvisoRequest $request)
    {
        $this->service->create($request->validated());
        return redirect()->route('admin.avisos.index')->with([
            'status' => true,
            'message' => 'Aviso creado con exito'
        ]);
    }
    public function edit(int $aviso)
    {
        $avisoObtenido = $this->service->show($aviso);
        return view('admin.avisos.edit', [
            'aviso' => new AvisoResource($avisoObtenido)->additional([
                'status' => true,
                'message' => 'Avisos obtenidos con exito'
            ]),
            'inputs' => $this->service->getFields()
        ]);
    }
    public function update(AvisoRequest $request, Aviso $aviso)
    {
        $this->service->update($request->validated(), $aviso);
        return redirect()->route('admin.avisos.index')->with([
            'status' => true,
            'message' => 'Aviso actualizado con exito'
        ]);
    }
    public function destroy(int $aviso)
    {
        $this->service->delete($aviso);
        return redirect()->route('admin.avisos.index')->with([
            'status' => true,
            'message' => 'Aviso eliminado con exito'
        ]);
    }
}
