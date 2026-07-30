@extends('layouts.app')

@section('content')
    <h1>Editar Aviso:</h1>
    <x-form :inputs="$inputs" :aviso="$aviso" parameter="aviso" resource="avisos" ruta="update" method="PUT"></x-form>
@endsection