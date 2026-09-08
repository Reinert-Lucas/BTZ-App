BTZ App

  Sistema de gestión de avisos, trabajos y clientes desarrollado con
  Laravel 12, Kotlin y MySQL.

Tecnologías utilizadas

  Backend            Frontend Web          App Móvil          Base de Datos
  ------------------ --------------------- ------------------ ---------------
  Laravel 12 (PHP)   Blade + Bootstrap 5   Kotlin (Android)   MySQL

Otras tecnologías: Sanctum · Vite · MVC · Service Layer

Características

Panel de Administración

-   Gestión de usuarios
-   Gestión de clientes
-   Gestión de avisos
-   Gestión de materiales
-   Dashboard con estadísticas
-   Búsqueda avanzada y paginación
-   Autenticación mediante sesiones

Aplicación móvil

-   Inicio de sesión
-   Consumo de la API REST
-   Consulta y gestión de avisos
-   Integración mediante Kotlin

API REST

-   Autenticación con Laravel Sanctum
-   Endpoints para usuarios, clientes, avisos, trabajos y materiales
-   Respuestas JSON estandarizadas

Arquitectura

    Kotlin App
         │
         │ HTTP / JSON
         ▼
    Laravel API REST
         │
    Service Layer
         │
    Eloquent ORM
         │
       MySQL

El panel de administración comparte la misma lógica de negocio mediante
los Services, utilizando vistas Blade para la interfaz web.

Estructura del proyecto

    app/
     ├── Http/
     │   ├── Controllers/
     │   └── Requests/
     ├── Models/
     └── Services/

    resources/
     ├── views/
     └── css/

    routes/
     ├── web.php
     └── api.php

Autores

Reinert Lucas Iván y Britez Medina Iván

Proyecto desarrollado con fines académicos como sistema integral de
gestión con panel web y aplicación móvil.
