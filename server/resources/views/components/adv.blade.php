{{-- FORMULARIO PARA LA BUSQUEDA AVANZADA --}}
@php
    $rutaCompleta = "admin.$ruta.index" 
@endphp
<form method="GET" action="{{ route($rutaCompleta) }}">
    <div id="filters">
        {{-- Filtro por defecto --}}
        <div class="mb-2">
            <label>Buscar por Nro</label>
            <input class="form-control" type="number" name="usuario_id">
        </div>
    </div>
    <button type="button" class="btn btn-secondary" id="add-filter">
        + Agregar filtro
    </button>
    <button class="btn btn-primary">
        Buscar
    </button>
</form>
<template id="filter-template">
    <div class="filter-row mb-2 d-flex gap-2">
        <select class="form-select filter-type">
            <option value="">Seleccionar...</option>
            @foreach ($filtros as $filtro)
                <option data-tipo="{{ $filtro['type'] }}" value="{{ $filtro['field'] }}"
                    data-options="{{ json_encode($filtro['options'] ?? []) }}">{{ $filtro['label'] }}</option>
            @endforeach
        </select>
        <div class="filter-value flex-grow-1">
        </div>
        <button type="button" class="btn btn-danger remove-filter">
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
            class="form-select"
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
                    class="form-control"
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