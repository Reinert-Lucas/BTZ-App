@extends('layouts.app')

@section('texto')
    <x-text title="Editar Cliente"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" :cliente="$cliente" parameter="cliente" resource="clientes" ruta="update" method="PUT"></x-form>
@endsection
