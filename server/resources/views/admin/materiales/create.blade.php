@extends('layouts.app')

@section('content')
    <h1>Crear Material:</h1>
    <a href={{ route('admin.dashboard') }}>Inicio</a>
    <x-form :inputs="$inputs" parameter="material" resource="materiales" ruta="store" method="POST"></x-form>
@endsection