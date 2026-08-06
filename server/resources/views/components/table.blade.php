<div class="table-responsive">
    {{ $rows->links() }}
    <table class="table table-striped table-hover align-middle">
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
                @endphp
                <tr>
                    @foreach ($columns as $column)
                        @if ($column['field'] === 'estado' && data_get($row, $column['field']) === 'finalizado')
                            <td>
                                {{ data_get($row, $column['field']) }}
                                <a href="{{ route('admin.trabajos.show', [$parameter => $id]) }}">Ver Trabajo</a>
                            </td>
                        @endif
                        <td>{{ data_get($row, $column['field']) }}</td>
                    @endforeach
                    <td class="text-center">
                        <a href="{{ route("admin.$resource.edit", [$parameter => $id]) }}" class="btn btn-warning btn-sm">
                            Editar
                        </a>
                        <form action="{{ route("admin.$resource.destroy", [$parameter => $id]) }}" method="POST"
                            class="d-inline" onsubmit="return confirm('¿Eliminar este registro?');">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="btn btn-danger btn-sm">
                                Borrar
                            </button>
                        </form>
                    </td>
                </tr>
            @empty
                <tr>
                    <td colspan="{{ count($columns) + 1 }}" class="text-center">
                        No hay registros.
                    </td>
                </tr>
            @endforelse
        </tbody>
    </table>
</div>