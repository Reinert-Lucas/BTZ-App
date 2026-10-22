@extends('layouts.app')
@section('texto')
    <x-text title="Detalles del Trabajo"></x-text>
@endsection
@section('content')
    <div class="work-detail">

        <div class="page-header">
            <h2>Trabajo N°{{ $trabajo->trabajo_id }}</h2>
            <div class="d-flex gap-2">
                <a href="{{ route('admin.trabajos.pdf', $trabajo->aviso_id) }}" type="button" target="_blank" rel="noopener" class="pdf-button">
                    <img src={{ asset('imgs/pdf.png') }} alt="PDF Icon" id="pdf-icon">
                    Descargar PDF
                </a>
                <a href="{{ url()->previous() }}" class="btn back-btn">
                    ← Volver
                </a>
            </div>
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
