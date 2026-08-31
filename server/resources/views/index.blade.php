<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('css/app.css') }}">
    <title>Login</title>
</head>

<body class="d-flex flex-column min-vh-100 login-body">
    {{-- Si ya hay un usuario logueado redirigir al dashboard --}}
    @if (auth()->check())
        <script>
            window.location.href = "{{ route('admin.dashboard') }}";
        </script>
    @endif
    <form action="{{ route('admin.login') }}" method="POST" class="login-form">
        <h1 class="login-form-title">BTZ-APP</h1>
        @csrf
        @method('POST')
        <h3 class="login-form-subtitle">Iniciar Sesión</h3>
        <p>Introduce tus credenciales para ingresar al sitio</p>
        <input name="dni" id="dni" type="text" inputmode="numeric" pattern="\d*" max="8"
            class="@error('dni') is-invalid @enderror form-control" placeholder="DNI (Sin puntos)">
        <input type="password" name="password" id="password" class="@error('dni') is-invalid @enderror form-control"
            placeholder="Contraseña">
        <input type="submit" value="Ingresar" class="btn btn-dark">
        @error('dni')
            <span class="form-text text-danger">{{ $message }}</span>
        @enderror
    </form>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>