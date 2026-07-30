@extends('layouts.app')

@section('content')
    <h1>Editar Usuario:</h1>
    <x-form :inputs="$inputs" :usuario="$usuario" parameter="usuario" resource="usuarios" ruta="update"
        method="PUT"></x-form>
@endsection