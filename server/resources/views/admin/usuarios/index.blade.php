@extends('layouts.app')

@section('texto')
    <x-text title="Gestion de Usuarios"></x-text>
@endsection
@section('content')
    <section class="btns-section">
        <x-adv :filtros="$filtros" ruta="usuarios" model="usuario"></x-adv>
        <a class="create-btn" href={{ route('admin.usuarios.create') }}>
            <img src="{{ asset('imgs/add.png') }}" alt="Crear Nuevo">
        </a>
    </section>
    <x-table :columns="$columns" :rows="$usuarios" parameter="usuario" resource="usuarios" />
@endsection