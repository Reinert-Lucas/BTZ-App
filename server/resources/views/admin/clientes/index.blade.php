@extends('layouts.app')
@section('texto')
    <x-text title="Gestion de Clientes"></x-text>
@endsection
@section('content')
    <section>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.clientes.create') }}>+</a>
        <x-adv :filtros="$filtros" ruta="clientes" model="cliente"></x-adv>
    </section>
    <x-table :columns="$columns" :rows="$clientes" parameter="cliente" resource="clientes" />
@endsection
