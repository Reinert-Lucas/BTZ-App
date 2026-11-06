<div class="table-responsive table-card">
    @php
    $rutaBase = "admin.$resource.index"
    @endphp
    <a href="{{ route($rutaBase) }}" class="btn show-all">Mostrar Todos</a>
    <a href="{{ route($rutaBase, ['eliminados' => 1]) }}" class="btn show-deleted">Mostrar Eliminados</a>
    <table class="admin-table">
        <thead>
            <tr>
                @foreach ($columns as $column)
                <th>{{ $column['label'] }}</th>
                @endforeach
                <th class="text-center">Acciones</th>
            </tr>
        </thead>

        <tbody>
            @forelse ($rows as $row)
            @php
            $id = data_get($row, $parameter . '_id');
            $eliminado = data_get($row, 'deleted_at')
            @endphp

            <tr 
            {{-- Si el resource es aviso, al clickear redirigir a admin.trabajos.show --}}
            @if ($resource === 'avisos')
                onclick="window.location='{{ route('admin.trabajos.show', [$parameter => $id]) }}'"
            @endif
            |>  
                @foreach ($columns as $column)
                @switch($column['field'])
                @case('estado')
                <td data-label="{{ $column['label'] }}">
                    <div class="cell-content">
                        <span class="status status-{{ data_get($row, 'estado') }}">
                            {{ ucfirst(data_get($row, 'estado')) }}
                        </span>

                        @if (data_get($row, 'estado') === 'finalizado')
                        <a href="{{ route('admin.trabajos.show', [$parameter => $id]) }}"
                            class="trabajo-btn">
                            Ver trabajo
                        </a>
                        @endif
                    </div>
                </td>
                @break

                @case('urgencia')
                <td data-label="{{ $column['label'] }}">
                    <span class="urgency urgency-{{ data_get($row, 'urgencia') }}">
                        {{ ucfirst(data_get($row, 'urgencia')) }}
                    </span>
                </td>
                @break

                @case('fecha')
                <td data-label="{{ $column['label'] }}">
                    {{ \Carbon\Carbon::parse(data_get($row, $column['field']))->format('d/m/Y') }}
                </td>
                @break

                @case('mensaje')
                <td data-label="{{ $column['label'] }}" class="td-mensaje">
                    {{ Str::limit(data_get($row, $column['field']), 50) }}
                </td>
                @break

                @case('hora')
                <td data-label="{{ $column['label'] }}">
                    {{ \Carbon\Carbon::parse(data_get($row, $column['field']))->format('H:i') }}
                </td>
                @break

                @default
                <td data-label="{{ $column['label'] }}">
                    {{ data_get($row, $column['field']) }}
                </td>
                @endswitch
                @endforeach

                <td class="text-center actions" data-label="Acciones">
                    <div class="action-buttons">
                        {{-- Si el registro está eliminado --}}
                        @if ($row->trashed())
                        <form action="{{ route("admin.$resource.restore", [$parameter => $id]) }}"
                            method="POST"
                            onsubmit="return confirm('¿Desea restaurar este registro?');">
                            @csrf
                            @method('PATCH')

                            <button type="submit" class="btn btn-success btn-sm"
                                aria-label="Restaurar">
                                <img src="{{ asset('imgs/restore.png') }}" alt="Restaurar">
                            </button>
                        </form>
                        <form action="{{ route("admin.$resource.forceDelete", [$parameter => $id]) }}"
                            method="POST"
                            onsubmit="return confirm('Esta acción es permanente y no se puede deshacer. ¿Eliminar para siempre?');">
                            @csrf
                            @method('DELETE')

                            <button type="submit" class="btn btn-danger btn-sm"
                                aria-label="Eliminar para siempre">
                                <img src="{{ asset('imgs/trash.png') }}" alt="Eliminar para siempre">
                            </button>
                        </form>
                        @else
                        <a href="{{ route("admin.$resource.edit", [$parameter => $id]) }}" class="btn btn-warning btn-sm" aria-label="Editar">
                            <img src="{{ asset('imgs/pen.png') }}" alt="Editar">
                        </a>
                        <form action="{{ route("admin.$resource.destroy", [$parameter => $id]) }}"
                            method="POST"
                            onsubmit="return confirm('¿Eliminar este registro?');">
                            @csrf
                            @method('DELETE')

                            <button type="submit" class="btn btn-danger btn-sm"
                                aria-label="Eliminar">
                                <img src="{{ asset('imgs/trash.png') }}" alt="Eliminar">
                            </button>
                        </form>
                        @endif
                    </div>
                </td>
            </tr>
            @empty
            <tr class="empty-row">
                <td colspan="{{ count($columns) + 1 }}" class="text-center">
                    No hay registros.
                </td>
            </tr>
            @endforelse
        </tbody>
    </table>

    <div class="admin-pagination">
        {{ $rows->links() }}
    </div>
</div>