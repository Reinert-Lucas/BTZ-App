@extends('layouts.app')

@section('texto')
    <x-text title="Editar Material"></x-text>
@endsection
@section('content')
    <x-form :inputs="$inputs" :material="$material" parameter="material" resource="materiales" ruta="update"
        method="PUT"></x-form>
@endsection
