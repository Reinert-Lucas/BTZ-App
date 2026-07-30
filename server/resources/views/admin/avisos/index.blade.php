@extends('layouts.app')

@section('content')
    <section>
        <h1>Gestion de Avisos</h1>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.avisos.create') }}>+</a>
    </section>
    <x-table :columns="$columns" :rows="$avisos" parameter="aviso" resource="avisos" />
@endsection