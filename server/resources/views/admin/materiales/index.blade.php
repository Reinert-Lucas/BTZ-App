@extends('layouts.app')

@section('content')
    <section>
        <h1>Gestion de Materiales</h1>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.materiales.create') }}>+</a>
    </section>
    <x-table :columns="$columns" :rows="$materiales" parameter="material" resource="materiales" />
@endsection