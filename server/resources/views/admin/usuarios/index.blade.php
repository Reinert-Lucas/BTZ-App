@extends('layouts.app')

@section('texto')
    <x-text title="Gestion de Usuarios" class="title-comp"></x-text>
@endsection
@section('content')
    <section>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.usuarios.create') }}>+</a>
        <x-adv :filtros="$filtros" ruta="usuarios" model="usuario"></x-adv>
    </section>
    <x-table :columns="$columns" :rows="$usuarios" parameter="usuario" resource="usuarios" />
@endsection