@extends('layouts.app')

@section('content')
    {{-- Si ya hay un usuario logueado redirigir al dashboard --}}
    @if (auth()->check())
        <script>
            window.location.href = "{{ route('admin.dashboard') }}";
        </script>
    @endif
    <h1>Login</h1>
    <form action="{{ route('admin.login') }}" method="POST">
        @csrf
        @method('POST')
        <label for="dni">DNI:</label>
        <input name="dni" id="dni" type="text" inputmode="numeric" pattern="\d*" max="8"
            class="@error('dni') is-invalid @enderror" placeholder="DNI (Sin puntos)">
        <label for="password">Contraseña:</label>
        <input type="password" name="password" id="password">
        <input type="submit" value="Ingresar">
        @error('dni')
            <span>{{ $message }}</span>
        @enderror
    </form>
@endsection