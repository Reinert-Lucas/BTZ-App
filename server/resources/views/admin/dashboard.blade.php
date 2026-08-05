@extends('layouts.app')

@section('content')
    <h1>Panel de Admin</h1>
    <form action="{{ route('admin.logout') }}" method="POST">
        @csrf
        @method('POST')
        <input type="submit" value="Cerrar Sesion">
    </form>
    <a href="{{ route('admin.me') }}">Me</a>
    <section>
        <a href={{ route("admin.usuarios.index") }}>Gestion de Usuarios</a>
        <a href={{ route("admin.avisos.index") }}>Gestion de Aviso</a>
        <a href={{ route("admin.materiales.index") }}>Gestion de Materiales</a>
        <a href={{ route("admin.clientes.index") }}>Gestion de Clientes</a>
        {{--
        Metricas a mostrar: (Actualizar al entrar al panel de admin)
        - Ultimos 5 trabajos realizados
        - Usuarios mas activos (5 o 10)
        - Materiales mas usados (con cantidades)
        --}}
        @foreach ($metricas as $metrica)
            <div>
                <h1>{{ $metrica['label'] }}</h1>
                <ul>
                    @foreach ($metrica['content'] as $content)
                        <li>{{ $content }}</li>
                    @endforeach
                </ul>
            </div>
        @endforeach
    </section>
@endsection