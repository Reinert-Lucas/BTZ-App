@extends('layouts.app')

@section('content')
    <h1>Crear Aviso:</h1>
    <a href={{ route('admin.dashboard') }}>Inicio</a>
    <x-form :inputs="$inputs" parameter="aviso" resource="avisos" ruta="store" method="POST"></x-form>
@endsection