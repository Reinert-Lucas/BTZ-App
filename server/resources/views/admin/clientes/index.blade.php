@extends('layouts.app')

@section('content')
    <section>
        <h1>Gestion de Clientes</h1>
        <a href={{ route('admin.dashboard') }}>Inicio</a>
        <a class="btn btn-primary" href={{ route('admin.clientes.create') }}>+</a>
    </section>
    <x-table :columns="$columns" :rows="$clientes" parameter="cliente" resource="clientes" />
@endsection