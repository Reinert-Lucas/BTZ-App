@extends('layouts.app')

@section('content')
    <h1>Editar Cliente:</h1>
    <x-form :inputs="$inputs" :cliente="$cliente" parameter="cliente" resource="clientes" ruta="update"
        method="PUT"></x-form>
@endsection