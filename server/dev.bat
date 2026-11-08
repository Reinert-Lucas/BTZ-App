@echo off
setlocal
cd /d "%~dp0"

where php >nul 2>nul
if errorlevel 1 (
    echo No se encontro PHP en PATH. Agrega PHP al PATH y vuelve a intentarlo.
    exit /b 1
)

start "BTZ - Servidor Laravel" cmd /k "php artisan serve --host=0.0.0.0"
start "BTZ - Cola de notificaciones" cmd /k "php artisan queue:listen --tries=1"

echo Servidor y cola iniciados en ventanas separadas.
echo Cierra esas ventanas para detener los procesos.
