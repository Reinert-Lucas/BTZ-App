@extends('layouts.app')

@section('content')
    <h1>Panel de Administración</h1>
    <section class="cards-section">
        <a class="admin-card" href={{ route("admin.usuarios.index") }}>Gestion de Usuarios</a>
        <a class="admin-card" href={{ route("admin.avisos.index") }}>Gestion de Aviso</a>
        <a class="admin-card" href={{ route("admin.materiales.index") }}>Gestion de Materiales</a>
        <a class="admin-card" href={{ route("admin.clientes.index") }}>Gestion de Clientes</a>
    </section>
    <section class="stats-section">
        @foreach ($metricas as $metrica)
            <section class="stat-card">
                <p class="stat-label">{{ $metrica['label'] }}</p>
                @switch($metrica['type'])
                    @case('trabajos')
                        <ul class="stats">
                            @foreach ($metrica['content'] as $content)
                                <li>
                                    <b>Trabajo: </b>{{ $content->trabajo_realizado }} <br>
                                    <b>Fecha: </b>{{ $content->aviso?->fecha }}
                                </li>
                            @endforeach
                        </ul>
                        @break
                    @case('usuarios')
                        <ul class="stats">
                            @foreach ($metrica['content'] as $content)
                                <li>
                                    <b>Nombre: </b>{{ $content->nombre }} <br>
                                    <b>Avisos Finalizados: </b>{{ $content->avisos_finalizados_count }}
                                </li>
                            @endforeach
                        </ul>
                        @break
                    @case('materiales')
                        <ul class="stats">
                            @foreach ($metrica['content'] as $content)
                                <li>
                                    <b>Material: </b>{{ $content->material->nombre }} <br>
                                    <b>Total Usado: </b>{{ $content->total }}
                                </li>
                            @endforeach
                        </ul>
                        @break
                @endswitch
            </section>
        @endforeach
    </section>
@endsection