BTZ App

Sistema de gestión de avisos y trabajos desarrollado como proyecto full
stack con Laravel 12, MySQL y un Panel de Administración Web en Blade.
Además, cuenta con una API REST preparada para ser consumida por una
aplicación móvil desarrollada en Kotlin.

Tecnologías

-   Laravel 12
-   PHP 8.3+
-   MySQL
-   Blade
-   Bootstrap 5
-   Sanctum
-   Vite

Funcionalidades

Panel de Administración

-   Gestión de usuarios
-   Gestión de clientes
-   Gestión de avisos
-   Gestión de materiales
-   Dashboard con estadísticas
-   Búsqueda avanzada y paginación
-   Autenticación por sesión para administradores

API REST

-   Login mediante Sanctum
-   Gestión de usuarios
-   Gestión de clientes
-   Gestión de avisos
-   Gestión de trabajos
-   Gestión de materiales
-   Respuestas en formato JSON

Instalación

1.  Clonar el repositorio.

2.  Instalar dependencias de PHP:

    composer install

3.  Instalar dependencias de Node:

    npm install

4.  Copiar el archivo de entorno:

    cp .env.example .env

5.  Generar la clave de la aplicación:

    php artisan key:generate

6.  Configurar la base de datos en .env.

7.  Ejecutar las migraciones:

    php artisan migrate

8.  Iniciar el proyecto:

    php artisan serve npm run dev

Estructura del proyecto

-   app/Http/Controllers → Controladores Web y API
-   app/Services → Lógica de negocio
-   app/Models → Modelos Eloquent
-   resources/views → Panel de administración
-   routes/web.php → Rutas del panel
-   routes/api.php → Endpoints REST

Arquitectura

El proyecto sigue una arquitectura MVC + Service Layer:

-   Controllers: reciben la petición y retornan vistas o respuestas
    JSON.
-   Services: contienen la lógica de negocio y consultas complejas.
-   Models: representan las entidades y relaciones de la base de datos.

Licencia

Proyecto desarrollado con fines académicos.
