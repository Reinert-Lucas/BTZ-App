@extends('layouts.app')

@section('content')
    <h1>Crear Usuario:</h1>
    <a href={{ route('admin.dashboard') }}>Inicio</a>
    <x-form :inputs="$inputs" parameter="usuario" resource="usuarios" ruta="store" method="POST"></x-form>
@endsection