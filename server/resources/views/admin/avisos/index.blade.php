@extends('layouts.app')

@section('texto')
    <x-text title="Gestion de Avisos"></x-text>
@endsection
@section('content')
    <section>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.avisos.create') }}>+</a>
        <x-adv :filtros="$filtros" ruta="avisos" model="aviso"></x-adv>
    </section>
    <x-table :columns="$columns" :rows="$avisos" parameter="aviso" resource="avisos" />
@endsection
