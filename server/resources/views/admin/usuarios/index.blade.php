@extends('layouts.app')

@section('content')
    <section>
        <h1>Gestion de Usuarios</h1>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.usuarios.create') }}>+</a>
    </section>
    <x-table :columns="$columns" :rows="$usuarios" parameter="usuario" resource="usuarios" />
@endsection