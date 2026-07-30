@extends('layouts.app')

@section('content')
    <h1>Panel de Admin</h1>
    <section>
        <a href={{ route("admin.usuarios.index") }}>Gestion de Usuarios</a>
        <a href={{ route("admin.avisos.index") }}>Gestion de Aviso</a>
        <a href={{ route("admin.materiales.index") }}>Gestion de Materiales</a>
        <a href={{ route("admin.clientes.index") }}>Gestion de Clientes</a>
    </section>
@endsection