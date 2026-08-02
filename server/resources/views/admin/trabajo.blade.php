@extends('layouts.app')

@section('content')
    <h1>Detalles del Trabajo Realizado</h1>
    <ul>
        <li>
            <h3>Desperfecto Encontrado:</h3>
            <p>{{ $trabajo->desperfecto }}</p>
        </li>
        <li>
            <h3>Reparaciones Hechas:</h3>
            <p>{{ $trabajo->trabajo_realizado }}</p>
        </li>
        <li>
            <h3>Operario a Cargo:</h3>
            <p>{{ $trabajo->aviso->usuario->nombre }}</p>
        </li>
        <li>
            <h3>Cliente:</h3>
            <p>{{ $trabajo->aviso->cliente->nombre }}</p>
        </li>
        <li>
            <h3>Materiales Usados:</h3>
            <ul>
                <table class="table">
                    <thead>
                        <th>Material</th>
                        <th>Cantidad</th>
                        <th>Detalle</th>
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
            </ul>
        </li>
    </ul>
@endsection