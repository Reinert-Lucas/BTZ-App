@extends('layouts.app')

@section('content')
    <section class="cards-section">
        <a class="admin-card" href={{ route("admin.usuarios.index") }}>Gestion de Usuarios</a>
        <a class="admin-card" href={{ route("admin.avisos.index") }}>Gestion de Aviso</a>
        <a class="admin-card" href={{ route("admin.materiales.index") }}>Gestion de Materiales</a>
        <a class="admin-card" href={{ route("admin.clientes.index") }}>Gestion de Clientes</a>
    </section>
    <section class="stats-section">
        @foreach($metricas as $metrica)
        <div class="stats-widget">
            <div class="widget-header">
                <h5>{{ $metrica['label'] }}</h5>
            </div>
            <div class="widget-body">
                @switch($metrica['type'])
                    @case('trabajos')
                        @foreach($metrica['content'] as $trabajo)
                            <div class="work-item">
                                <span class="badge-date">{{ $trabajo->aviso?->fecha }}</span>
                                <strong>{{ $trabajo->trabajo_realizado }}</strong>
                            </div>
                        @endforeach
                    @break
                    @case('usuarios')
                        @foreach($metrica['content'] as $i => $usuario)
                            <div class="ranking-item">
                                <div class="ranking-pos">{{ $i + 1 }}</div>
                                <div>
                                    <strong>{{ $usuario->nombre }}</strong>
                                    <small>{{ $usuario->avisos_finalizados_count }}{{ $usuario->avisos_finalizados_count === 1 ? ' trabajo' : ' trabajos' }}</small>
                                </div>
                            </div>
                        @endforeach
                    @break
                    @case('materiales')
                        @php
                            $max = $metrica['content']->max('total');
                        @endphp
                        @foreach($metrica['content'] as $material)
                            <div class="material-item">
                                <div class="d-flex justify-content-between">
                                    <span>{{ $material->material->nombre }}</span>
                                    <strong>{{ $material->total }}</strong>
                                </div>
                                <div class="progress mt-1">
                                    <div class="progress-bar"
                                         style="width: {{ ($material->total / $max) * 100 }}%">
                                    </div>
                                </div>
                            </div>
                        @endforeach
                    @break
                @endswitch
            </div>
        </div>
    @endforeach
    </section>
@endsection