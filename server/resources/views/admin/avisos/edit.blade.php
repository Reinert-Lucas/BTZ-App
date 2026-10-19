@extends('layouts.app')

@section('texto')
    <x-text title="Editar Avisos"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" :aviso="$aviso" parameter="aviso" resource="avisos" ruta="update" method="PUT"></x-form>
@endsection
