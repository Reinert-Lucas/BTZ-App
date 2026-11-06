@extends('layouts.app')
@section('texto')
    @if ($esTrabajo)
        <x-text title="Detalles del Trabajo"></x-text>
    @else
        <x-text title="Detalles del Aviso"></x-text>
    @endif
@endsection
@section('content')
    <div class="work-detail">

        <div class="page-header">
            <h2>
                {{ $esTrabajo ? "Trabajo N° " . $aviso->trabajo->trabajo_id : "Aviso N°" . $aviso->aviso_id }}
            </h2>
            <div class="d-flex gap-2">
                <a href="{{ route('admin.trabajos.pdf', $aviso->aviso_id) }}" type="button" target="_blank" rel="noopener"
                    class="pdf-button">
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
                <strong>{{ $aviso->usuario->nombre }}</strong>
            </div>

            <div class="info-item">
                <span>Cliente</span>
                <strong>{{ $aviso->cliente->nombre }}</strong>
            </div>

            <div class="info-item">
                <span>Fecha</span>
                <strong>{{ \Carbon\Carbon::parse(data_get($aviso, 'fecha'))->format('d/m/Y') }}</strong>
            </div>

            @if ($esTrabajo)
                <div class="info-item">
                    <span>Estado</span>
                    <span class="status status-finalizado">Finalizado</span>
                </div>
            @else
                <div class="info-item">
                    <span>Estado</span>
                    <span class="status status-{{ data_get($aviso, 'estado') }}">{{ data_get($aviso, 'estado') }}</span>
                </div>
            @endif


        </div>

        <div class="detail-card">
            <h5>Mensaje del Trabajo</h5>
            <p>{{ $aviso->mensaje }}</p>
        </div>

        <div class="detail-card">
            <h5>Observaciones del Administrador</h5>
            <p>{{ $aviso->observacion }}</p>
        </div>

        @if ($esTrabajo)
            <div class="detail-card">
                <h5>Desperfecto encontrado</h5>
                <p>{{ $aviso->trabajo->desperfecto }}</p>
            </div>

            <div class="detail-card">
                <h5>Reparaciones realizadas</h5>
                <p>{{ $aviso->trabajo->trabajo_realizado }}</p>
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
                        @foreach ($aviso->trabajo->materiales as $material)
                            <tr>
                                <td>{{ $material->nombre }}</td>
                                <td>{{ $material->pivot->cantidad }}</td>
                                <td>{{ $material->detalle }}</td>
                            </tr>
                        @endforeach
                    </tbody>

                </table>

            </div>
        @endif

    </div>
@endsection