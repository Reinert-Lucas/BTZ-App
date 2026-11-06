@php
    $rutaCompleta = "admin.$ruta.index";
@endphp
<form method="GET" action="{{ route($rutaCompleta) }}" class="adv-form">
    <div id="filters">
        {{-- Filtro por defecto --}}
        <div class="adv-form-search">
            <input class="form-control number-search" type="number" name="{{ $model }}_id"
                placeholder="Buscar por número">
            <button type="button" id="add-filter">
                <img src="{{ asset('imgs/add.png') }}" alt="Agregar filtro"> Agregar
            </button>
            <button>
                <img src="{{ asset('imgs/search.png') }}" alt="Buscar"> Buscar
            </button>
        </div>
    </div>
</form>
<template id="filter-template">
    <div class="filter-row">
        <select class="form-select filter-type">
            <option value="">Seleccionar...</option>
            @foreach ($filtros as $filtro)
                {{-- Opciones como lista [valor, etiqueta]: conserva el orden alfabético --}}
                <option data-tipo="{{ $filtro['type'] }}" value="{{ $filtro['field'] }}"
                    data-options="{{ json_encode(collect($filtro['options'] ?? [])->map(fn($etiqueta, $valor) => [$valor, $etiqueta])->values()) }}">
                    {{ $filtro['label'] }}
                </option>
            @endforeach
        </select>
        <div class="filter-value"></div>
        <button type="button" class="remove-filter">X</button>
    </div>
</template>

<script>
    const filters = document.getElementById('filters');
    const template = document.getElementById('filter-template');

    document.getElementById('add-filter').addEventListener('click', () => {
        filters.appendChild(template.content.cloneNode(true));
    });

    filters.addEventListener('change', (e) => {
        if (!e.target.classList.contains('filter-type')) {
            return;
        }

        const container = e.target.closest('.filter-row').querySelector('.filter-value');
        const opcion = e.target.selectedOptions[0];
        const campo = e.target.value;

        container.replaceChildren();
        if (!campo) {
            return;
        }

        switch (opcion.dataset.tipo) {
            case 'select': {
                const select = document.createElement('select');
                select.className = 'form-select select-adpt';
                select.name = campo;
                select.add(new Option('Seleccionar...', ''));
                // new Option() trata la etiqueta como texto: nunca interpreta HTML.
                for (const [valor, etiqueta] of JSON.parse(opcion.dataset.options || '[]')) {
                    select.add(new Option(etiqueta, valor));
                }
                container.appendChild(select);
                break;
            }
            case 'checkbox': {
                const wrapper = document.createElement('div');
                wrapper.className = 'form-check';
                const input = document.createElement('input');
                input.type = 'checkbox';
                input.className = 'form-check-input';
                input.value = '1';
                input.name = campo;
                input.id = campo;
                wrapper.appendChild(input);
                container.appendChild(wrapper);
                break;
            }
            default: {
                const input = document.createElement('input');
                input.className = 'form-control text-input';
                input.type = opcion.dataset.tipo;
                input.name = campo;
                container.appendChild(input);
            }
        }
    });

    filters.addEventListener('click', (e) => {
        if (e.target.classList.contains('remove-filter')) {
            e.target.closest('.filter-row').remove();
        }
    });
</script>