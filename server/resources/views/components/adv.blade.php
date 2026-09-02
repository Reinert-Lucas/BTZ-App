{{-- FORMULARIO PARA LA BUSQUEDA AVANZADA --}}
@php
    $rutaCompleta = "admin.$ruta.index" 
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
                <option data-tipo="{{ $filtro['type'] }}" value="{{ $filtro['field'] }}"
                    data-options="{{ json_encode($filtro['options'] ?? []) }}">{{ $filtro['label'] }}</option>
            @endforeach
        </select>
        <div class="filter-value">
        </div>
        <button type="button" class="remove-filter">
            X
        </button>
    </div>
</template>

<script>
    const filters = document.getElementById("filters");
    const template = document.getElementById("filter-template");
    document
        .getElementById("add-filter")
        .addEventListener("click", () => {
            const clone = template.content.cloneNode(true);
            filters.appendChild(clone);
        });

    filters.addEventListener("change", e => {
        if (!e.target.classList.contains("filter-type")) {
            return;
        }
        const container =
            e.target.parentElement.querySelector(".filter-value");
        switch (e.target.selectedOptions[0].dataset.tipo) {
            case "select":
                const options = JSON.parse(
                    e.target.selectedOptions[0].dataset.options
                );
                let html = `
        <select
            class="form-select select-adpt"
            name="${e.target.value}">
    `;
                for (const [value, label] of Object.entries(options)) {
                    html += `
            <option value="${value}">
                ${label}
            </option>
        `;
                }
                html += `</select>`;
                container.innerHTML = html;
                break;
            case "checkbox":
                container.innerHTML = `
                <div class="form-check">
                    <input
                        type="checkbox"
                        class="form-check-input"
                        value="1"
                        name="${e.target.value}"
                        id="${e.target.value}">
                </div>
                `
                break;
            default:
                container.innerHTML = `
                <input
                    class="form-control text-input"
                    type="${e.target.selectedOptions[0].dataset.tipo}"
                    name="${e.target.value}">
            `;
                break;
        }
    });
    filters.addEventListener("click", e => {
        if (e.target.classList.contains("remove-filter")) {
            e.target
                .closest(".filter-row")
                .remove();
        }
    });
</script>