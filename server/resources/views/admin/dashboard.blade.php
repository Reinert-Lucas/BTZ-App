@extends('layouts.app')

@section('content')
    <section class="cards-section">
        <article class="admin-card" data-route="{{ route('admin.usuarios.index') }}">
            <img src="{{ asset('imgs/tarjeta_usuarios.png') }}" alt="Gestion de Usuarios" class="admin-card-img">
        </article>
        <article class="admin-card" data-route="{{ route('admin.avisos.index') }}">
            <img src="{{ asset('imgs/tarjeta_avisos.png') }}" alt="Gestion de Avisos" class="admin-card-img">
        </article>
        <article class="admin-card" data-route="{{ route('admin.materiales.index') }}">
            <img src="{{ asset('imgs/tarjeta_materiales.png') }}" alt="Gestion de Materiales" class="admin-card-img">
        </article>
        <article class="admin-card" data-route="{{ route('admin.clientes.index') }}">
            <img src="{{ asset('imgs/tarjeta_clientes.png') }}" alt="Gestion de Clientes" class="admin-card-img">
        </article>
    </section>
    <section class="stats-section">
        @foreach ($metricas as $metrica)
            <div class="stats-widget">
                <div class="widget-header">
                    <h5>{{ $metrica['label'] }}</h5>
                </div>
                <div class="widget-body">
                    @switch($metrica['type'])
                        @case('trabajos')
                            @foreach ($metrica['content'] as $trabajo)
                                <div class="work-item">
                                    <span class="badge-date">
                                        {{ \Carbon\Carbon::parse($trabajo->aviso->fecha)->format('d/m/Y') }}</span>
                                    <strong>{{ $trabajo->trabajo_realizado }}</strong>
                                </div>
                            @endforeach
                        @break

                        @case('usuarios')
                            @foreach ($metrica['content'] as $i => $usuario)
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
                            @foreach ($metrica['content'] as $material)
                                <div class="material-item">
                                    <div class="d-flex justify-content-between">
                                        <span>{{ $material->material->nombre }}</span>
                                        <strong>{{ $material->total }}</strong>
                                    </div>
                                    <div class="progress mt-1">
                                        <div class="progress-bar" style="width: {{ ($material->total / $max) * 100 }}%">
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

@section('scripts')
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const cards = document.querySelectorAll('.admin-card');
            cards.forEach(card => {
                card.addEventListener('click', function() {
                    const route = this.getAttribute('data-route');
                    if (route) {
                        window.location.href = route;
                    }
                });
            });
        });
    </script>