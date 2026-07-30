@extends('layouts.app')

@section('content')
    <h1>Editar Material:</h1>
    <x-form :inputs="$inputs" :material="$material" parameter="material" resource="materiales" ruta="update"
        method="PUT"></x-form>
@endsection