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
        {{-- <a href={{ route("admin.trabajos.index") }}>Gestion de Trabajos</a>
        --}}
    </section>
@endsection