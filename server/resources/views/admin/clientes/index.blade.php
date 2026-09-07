@extends('layouts.app')
@section('texto')
    <x-text title="Gestion de Clientes"></x-text>
@endsection
@section('content')
    <section class="btns-section">
        <x-adv :filtros="$filtros" ruta="clientes" model="cliente"></x-adv>
        <a class="create-btn" href={{ route('admin.clientes.create') }}>
            <img src="{{ asset('imgs/add.png') }}" alt="Crear Nuevo">
        </a>
    </section>
    <x-table :columns="$columns" :rows="$clientes" parameter="cliente" resource="clientes" />
@endsection