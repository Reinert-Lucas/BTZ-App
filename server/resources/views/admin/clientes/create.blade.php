@extends('layouts.app')

@section('texto')
    <x-text title="Crear Cliente"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" parameter="cliente" resource="clientes" ruta="store" method="POST"></x-form>
@endsection
