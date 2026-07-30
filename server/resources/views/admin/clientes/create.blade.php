@extends('layouts.app')

@section('content')
    <h1>Crear Cliente:</h1>
    <a href={{ route('admin.dashboard') }}>Inicio</a>
    <x-form :inputs="$inputs" parameter="cliente" resource="clientes" ruta="store" method="POST"></x-form>
@endsection