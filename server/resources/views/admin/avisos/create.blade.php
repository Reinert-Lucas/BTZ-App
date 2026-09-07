@extends('layouts.app')

@section('texto')
    <x-text title="Crear Aviso"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" parameter="aviso" resource="avisos" ruta="store" method="POST"></x-form>
@endsection
