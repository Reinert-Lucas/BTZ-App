@extends('layouts.app')
@section('texto')
    <x-text title="Detalles del Trabajo"></x-text>
@endsection
@section('content')
    <div class="work-detail">

        <div class="page-header">
            <h2>Trabajo N°{{ $trabajo->trabajo_id }}</h2>
            <a href="{{ url()->previous() }}" class="btn back-btn">
                ← Volver
            </a>
        </div>

        <div class="detail-card info-grid">

            <div class="info-item">
                <span>Operario</span>
                <strong>{{ $trabajo->aviso->usuario->nombre }}</strong>
            </div>

            <div class="info-item">
                <span>Cliente</span>
                <strong>{{ $trabajo->aviso->cliente->nombre }}</strong>
            </div>

            <div class="info-item">
                <span>Fecha</span>
                <strong>{{ \Carbon\Carbon::parse(data_get($trabajo->aviso, 'fecha'))->format('d/m/Y') }}</strong>
            </div>

            <div class="info-item">
                <span>Estado</span>
                <span class="status status-finalizado">Finalizado</span>
            </div>

        </div>

        <div class="detail-card">
            <h5>Desperfecto encontrado</h5>
            <p>{{ $trabajo->desperfecto }}</p>
        </div>

        <div class="detail-card">
            <h5>Reparaciones realizadas</h5>
            <p>{{ $trabajo->trabajo_realizado }}</p>
        </div>

        <div class="detail-card">

            <h5>Materiales utilizados</h5>

            <table class="admin-table mt-3">

                <thead>
                    <tr>
                        <th>Material</th>
                        <th>Cantidad</th>
                        <th>Detalle</th>
                    </tr>
                </thead>

                <tbody>
                    @foreach ($trabajo->materiales as $material)
                        <tr>
                            <td>{{ $material->nombre }}</td>
                            <td>{{ $material->pivot->cantidad }}</td>
                            <td>{{ $material->detalle }}</td>
                        </tr>
                    @endforeach
                </tbody>

            </table>

        </div>

    </div>
@endsection
