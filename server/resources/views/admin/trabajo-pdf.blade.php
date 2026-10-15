<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reporte Trabajo N°{{ $trabajo->trabajo_id }}</title>
    <style>
        @page {
            size: A4;
            margin: 18mm;
        }

        body {
            color: #202b35;
            font-family: Arial, sans-serif;
            font-size: 12px;
            line-height: 1.5;
            margin: 0 auto;
            max-width: 800px;
            background-color: #F0F0F0;
        }

        .toolbar {
            display: flex;
            justify-content: flex-end;
            margin: 20px 0;
        }

        .toolbar button {
            background: #2F2483;
            border: 0;
            border-radius: 4px;
            color: #fff;
            cursor: pointer;
            font-size: 14px;
            padding: 10px 16px;
        }

        header {
            border-bottom: 3px solid #4839b8;
            margin-bottom: 24px;
            padding-bottom: 16px;
        }

        h1 {
            font-size: 24px;
            margin: 0 0 4px;
        }

        .subtitle,
        .muted {
            color: #4839b8;
        }

        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 14px 24px;
            margin: 20px 0 28px;
        }

        .info-label {
            color: #64727e;
            display: block;
            font-size: 10px;
            font-weight: bold;
            text-transform: uppercase;
        }

        .info-value {
            font-size: 13px;
        }

        section {
            margin: 24px 0;
        }

        h2 {
            border-bottom: 1px solid #d7dfe3;
            color: #4839b8;
            font-size: 15px;
            margin: 0 0 10px;
            padding-bottom: 6px;
        }

        p {
            margin: 0;
            white-space: pre-wrap;
        }

        table {
            border-collapse: collapse;
            width: 100%;
        }

        th,
        td {
            border-bottom: 1px solid #d7dfe3;
            padding: 9px 8px;
            text-align: left;
            vertical-align: top;
        }

        th {
            background: #eef4f3;
            font-size: 10px;
            text-transform: uppercase;
        }

        tr {
            break-inside: avoid;
        }

        footer {
            border-top: 1px solid #d7dfe3;
            color: #64727e;
            font-size: 10px;
            margin-top: 36px;
            padding-top: 10px;
        }

        @media print {
            .toolbar {
                display: none;
            }

            body {
                max-width: none;
            }
        }
    </style>
</head>

<body>
    {{-- Vista PDF de los resultados de un trabajo, abre dialogo de impresion para descargarlo --}}

    <div class="toolbar">
        <button type="button" onclick="window.print()">Volver a Descargar</button>
    </div>

    <header>
        <h1>Reporte de trabajo N°{{ $trabajo->trabajo_id }}</h1>
        <div class="subtitle">BTZ-APP · Trabajo finalizado</div>
    </header>

    <div class="info-grid">
        <div>
            <span class="info-label">Operario</span>
            <span class="info-value">{{ $trabajo->aviso->usuario->nombre }}</span>
        </div>
        <div>
            <span class="info-label">Cliente</span>
            <span class="info-value">{{ $trabajo->aviso->cliente->nombre }}</span>
        </div>
        <div>
            <span class="info-label">Fecha</span>
            <span class="info-value">{{ \Carbon\Carbon::parse(data_get($trabajo->aviso, 'fecha'))->format('d/m/Y') }}</span>
        </div>
        <div>
            <span class="info-label">Estado</span>
            <span class="info-value">Finalizado</span>
        </div>
    </div>

    <section>
        <h2>Desperfecto encontrado</h2>
        <p>{{ $trabajo->desperfecto }}</p>
    </section>

    <section>
        <h2>Reparaciones realizadas</h2>
        <p>{{ $trabajo->trabajo_realizado }}</p>
    </section>

    <section>
        <h2>Materiales utilizados</h2>
        <table>
            <thead>
                <tr>
                    <th>Material</th>
                    <th>Cantidad</th>
                    <th>Detalle</th>
                </tr>
            </thead>
            <tbody>
                @forelse ($trabajo->materiales as $material)
                    <tr>
                        <td>{{ $material->nombre }}</td>
                        <td>{{ $material->pivot->cantidad }}</td>
                        <td>{{ $material->detalle }}</td>
                    </tr>
                @empty
                    <tr>
                        <td colspan="3" class="muted">No se utilizaron materiales.</td>
                    </tr>
                @endforelse
            </tbody>
        </table>
    </section>

    <footer>Reporte generado el {{ now()->format('d/m/Y H:i') }}</footer>

    <script>
        window.addEventListener('load', () => {
            window.print();
        });
    </script>
</body>

</html>