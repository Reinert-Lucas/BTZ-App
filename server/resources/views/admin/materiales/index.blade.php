@extends('layouts.app')

@section('texto')
    <x-text title="Gestion de Materiales"></x-text>
@endsection
@section('content')
    <section class="btns-section">
        <x-adv :filtros="$filtros" ruta="materiales" model="material"></x-adv>
        <a class="create-btn" href={{ route('admin.materiales.create') }}>
            <img src="{{ asset('imgs/add.png') }}" alt="Crear Nuevo">
        </a>
    </section>
    <x-table :columns="$columns" :rows="$materiales" parameter="material" resource="materiales" />
@endsection