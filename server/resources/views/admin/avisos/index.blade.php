@extends('layouts.app')

@section('texto')
    <x-text title="Gestion de Avisos"></x-text>
@endsection
@section('content')
    <section class="btns-section">
        <x-adv :filtros="$filtros" ruta="avisos" model="aviso"></x-adv>
        <a class="create-btn" href={{ route('admin.avisos.create') }}>
            <img src="{{ asset('imgs/add.png') }}" alt="Crear Nuevo">
        </a>
    </section>
    <x-table :columns="$columns" :rows="$avisos" parameter="aviso" resource="avisos" />
@endsection