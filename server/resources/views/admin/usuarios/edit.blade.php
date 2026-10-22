@extends('layouts.app')

@section('texto')
    <x-text title="Editar Usuario"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" :usuario="$usuario" parameter="usuario" resource="usuarios" ruta="update" method="PUT"></x-form>
@endsection
