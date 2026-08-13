<?php

namespace App\Http\Controllers;

use App\Http\Requests\MaterialRequest;
use App\Http\Resources\MaterialResource;
use App\Models\Material;
use App\Services\MaterialService;
use Illuminate\Http\Request;

class WebMaterialController extends Controller
{
    private MaterialService $service;
    public function __construct(MaterialService $service)
    {
        $this->service = $service;
    }
    public function index(Request $request)
    {
        $materiales = $this->service->index($request);
        $columns = [
            [
                'label' => 'Nro',
                'field' => 'material_id'
            ],
            [
                'label' => 'Nombre',
                'field' => 'nombre',
            ],
            [
                'label' => 'Detalle',
                'field' => 'detalle',
            ]
        ];
        $filters = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ]
        ];
        return view('admin.materiales.index', [
            'materiales' => MaterialResource::collection($materiales),
            'columns' => $columns,
            'filtros' => $filters
        ]);
    }
    public function show()
    {
        //
    }
    public function create()
    {
        $inputs = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ],
            [
                'label' => 'Detalle',
                'field' => 'detalle',
                'type' => 'text'
            ]
        ];
        return view('admin.materiales.create', ['inputs' => $inputs]);
    }
    public function store(MaterialRequest $request)
    {
        $this->service->create($request->validated());
        return redirect()->route('admin.materiales.index')->with([
            'status' => true,
            'message' => 'Material creado con exito'
        ]);
    }
    public function edit(int $material)
    {
        $inputs = [
            [
                'label' => 'Nombre',
                'field' => 'nombre',
                'type' => 'text'
            ],
            [
                'label' => 'Detalle',
                'field' => 'detalle',
                'type' => 'text'
            ]
        ];
        $materialObtenido = $this->service->show($material);
        return view('admin.materiales.edit', [
            'material' => new MaterialResource($materialObtenido)->additional([
                'status' => true,
                'message' => 'Material obtenido con exito'
            ]),
            'inputs' => $inputs
        ]);
    }
    public function update(MaterialRequest $request, Material $material)
    {
        $this->service->update($request->validated(), $material);
        return redirect()->route('admin.materiales.index')->with([
            'status' => true,
            'message' => 'Material actualizado con exito'
        ]);
    }
    public function destroy(int $material)
    {
        $this->service->delete($material);
        return redirect()->route('admin.materiales.index')->with([
            'status' => true,
            'message' => 'Material eliminado con exito'
        ]);
    }
}
