<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('assets/css/styles.css') }}">
    @yield('extra_css')
    <title>@yield('title', 'Panel de Administracion')</title>
</head>

<body class="d-flex flex-column min-vh-100">
    <main class="flex-grow-1 py-4">
        @yield('content')
    </main>
    <footer class="py-3 bg-white border-top mt-auto">
        <div class="container text-center">
            <span class="text-muted small">&copy; {{ date('Y') }} BTZ-APP ADMIN PANEL</span>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    @yield('scripts')
</body>

</html>