<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="{{ asset('css/app.css') }}">
    @yield('extra_css')
    <title>@yield('title', 'Panel de Administracion')</title>
</head>

<body class="d-flex flex-column min-vh-100">
    <header>
        <div class="title-div">
            <img src="{{ asset('imgs/logo.png') }}" alt="Logo" class="img-fluid" style="max-height: 80px;">
            <a href={{ route("admin.dashboard") }} class="title">BTZ-APP</a>
            @yield('texto')
        </div>
        <div class="user-div">
            <button class="user-btn" data-bs-toggle="modal" data-bs-target="#userModal" id="user-btn">
                <img src="{{ asset('imgs/user.png') }}" alt="User" class="img-fluid user" style="max-height: 50px;">
            </button>
        </div>
    </header>
    <x-modal></x-modal>
    <main class=" flex-grow-1 py-4">
        @yield('content')
    </main>
    <footer class="py-3 bg-white border-top mt-auto">
        <div class="container text-center">
            <span class="text-muted small">&copy; {{ date('Y') }} BTZ-APP ADMIN PANEL</span>
        </div>
    </footer>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    {{-- POR AHORA NO SE NECESITA
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.8/dist/umd/popper.min.js"
        integrity="sha384-I7E8VVD/ismYTF4hNIPjVp/Zjvgyol6VFvRkX/vR+Vc4jQkC+hVqc2pM8ODewa9r"
        crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.min.js"
        integrity="sha384-G/EV+4j2dNv+tEPo3++6LCgdCROaejBqfUeNjuKAiuXbjrxilcCdDz6ZAVfHWe1Y"
        crossorigin="anonymous"></script> --}}
    @yield('scripts')
    <script>
        const modal = document.getElementById('userModal');
        const body = document.getElementById('modal-body');
        const userBtn = document.getElementById('user-btn')
            .addEventListener('click', async () => {
                try {
                    const res = await fetch('http://127.0.0.1:8000/admin/me', {
                        method: 'GET',
                        credentials: "same-origin",
                    });
                    if (!res.ok) {
                        throw new Error("Error al obtener datos");
                    }
                    const userData = await res.json();
                    body.innerHTML = `
                    <h5><b>Nombre:</b> ${userData.user.nombre}</h5>
                    <h5><b>Rol:</b> ${userData.user.rol}</h5>
                    <h5><b>Teléfono:</b> ${userData.user.telefono}</h5>
                    `;
                } catch (error) {
                    body.innerHTML = `
                        <div class="alert alert-danger">
                            No se pudieron cargar los datos.
                        </div>
                    `;
                }
            });
    </script>
</body>

</html>