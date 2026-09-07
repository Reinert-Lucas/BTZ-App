@extends('layouts.app')

@section('texto')
    <x-text title="Crear Usuario"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" parameter="usuario" resource="usuarios" ruta="store" method="POST"></x-form>
@endsection
