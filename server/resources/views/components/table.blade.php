<div class="table-responsive table-card">
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
                @endphp
                <tr>
                    @foreach ($columns as $column)
                        @switch($column['field'])
                            @case('estado')
                                <td>
                                    <span class="status status-{{ data_get($row, 'estado') }}">
                                        {{ ucfirst(data_get($row, 'estado')) }}
                                    </span>
                                    @if (data_get($row, 'estado') === 'finalizado')
                                        <br>
                                        <a href="{{ route('admin.trabajos.show', [$parameter => $id]) }}" class="trabajo-btn">
                                            Ver trabajo
                                        </a>
                                    @endif
                                </td>
                            @break

                            @case('urgencia')
                                <td>
                                    <span class="urgency urgency-{{ data_get($row, 'urgencia') }}">
                                        {{ ucfirst(data_get($row, 'urgencia')) }}
                                    </span>
                                </td>
                            @break

                            @case('fecha')
                                <td>
                                    {{ \Carbon\Carbon::parse(data_get($row, $column['field']))->format('d/m/Y') }}
                                </td>
                            @break

                            @default
                                <td>
                                    {{ data_get($row, $column['field']) }}
                                </td>
                        @endswitch
                    @endforeach
                    <td class="text-center actions">
                        <a href="{{ route("admin.$resource.edit", [$parameter => $id]) }}" class="btn btn-warning btn-sm">
                            <img src="{{ asset('imgs/pen.png') }}" alt="Editar">
                        </a>
                        <form action="{{ route("admin.$resource.destroy", [$parameter => $id]) }}" method="POST"
                            class="d-inline" onsubmit="return confirm('¿Eliminar este registro?');">
                            @csrf
                            @method('DELETE')
                            <button type="submit" class="btn btn-danger btn-sm">
                                <img src="{{ asset('imgs/trash.png') }}" alt="Eliminar">
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
        <div class="admin-pagination">
            {{ $rows->links() }}
        </div>
    </div>
