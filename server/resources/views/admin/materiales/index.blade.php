@extends('layouts.app')

@section('texto')
    <x-text title="Gestion de Materiales"></x-text>
@endsection
@section('content')
    <section>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.materiales.create') }}>+</a>
        <x-adv :filtros="$filtros" ruta="materiales" model="material"></x-adv>
    </section>
    <x-table :columns="$columns" :rows="$materiales" parameter="material" resource="materiales" />
@endsection