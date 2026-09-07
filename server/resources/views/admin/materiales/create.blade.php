@extends('layouts.app')

@section('texto')
    <x-text title="Crear Material"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" parameter="material" resource="materiales" ruta="store" method="POST"></x-form>
@endsection
