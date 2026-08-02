@extends('layouts.app')

@section('content')
    <h1>Acceso denegado</h1>
    <p>No tienes permisos para acceder a esta página.</p>
    <form action="{{ route('admin.logout') }}" method="POST">
        @csrf
        @method('POST')
        <input type="submit" value="Volver al login">
    </form>
@endsection