@extends('layouts.app')

@section('content')
    <h1 class="text-center">Acceso denegado</h1>
    <h3 class="text-center">
        <strong> El apartado web es solo para personal Administrativo </strong>
    </h3>

    <p class="text-center">
        No tienes permisos para acceder a esta página. <br />
        Puedes descargar la App para tu movil haciendo click <a
            href="https://drive.google.com/file/d/1WtEN9wIx71jh53-zsv9owVm_hpqNJRbP/view?usp=sharing">aquí</a>. <br />
    </p>
    <form action="{{ route('admin.logout') }}" method="POST" class="text-center">
        @csrf
        @method('POST')
        <input type="submit" class="btn modal-btn" value="Volver al login">
    </form>
@endsection
