@php
    $isUpdate = $ruta === 'update';
    $id = $isUpdate ? ${$parameter}->{$parameter . '_id'} : null;
@endphp

<div class="admin-form-card">
    <form method="POST"
        action="{{ $isUpdate ? route("admin.$resource.$ruta", [$parameter => $id]) : route("admin.$resource.$ruta") }}">
        @csrf
        @method($method)
        <div class="row g-3 abc">
            @foreach ($inputs as $input)
                <div class="{{ $input['type'] === 'textarea' ? 'col-12' : 'col-md-6' }}">
                    @if ($input['type'] === 'checkbox')
                        <div class="form-check mt-4">
                            <input class="form-check-input" type="checkbox" id="{{ $input['field'] }}"
                                name="{{ $input['field'] }}" value="1" @checked(old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : false))>
                            <label class="form-check-label" for="{{ $input['field'] }}">
                                {{ $input['label'] }}
                            </label>
                        </div>
                    @else
                        <label class="form-label fw-semibold" for="{{ $input['field'] }}">
                            {{ $input['label'] }}
                        </label>
                        @switch($input['type'])
                            @case('select')
                                <select class="form-select" id="{{ $input['field'] }}" name="{{ $input['field'] }}">
                                    @foreach ($input['options'] as $value => $text)
                                        <option value="{{ $value }}" @selected(old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : null) == $value)>
                                            {{ $text }}
                                        </option>
                                    @endforeach
                                </select>
                            @break

                            @case('textarea')
                                <textarea class="form-control" id="{{ $input['field'] }}" name="{{ $input['field'] }}" rows="4"
                                    placeholder="{{ $input['label'] }}">{{ old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : '') }}</textarea>
                            @break

                            @default
                                @if ($input['field'] === 'password')
                                    <input class="form-control" type="{{ $input['type'] }}" id="{{ $input['field'] }}"
                                        name="{{ $input['field'] }}" placeholder="{{ $input['label'] }}">
                                    @break
                                @endif
                                <input class="form-control" type="{{ $input['type'] }}" id="{{ $input['field'] }}"
                                    name="{{ $input['field'] }}" placeholder="{{ $input['label'] }}"
                                    value="{{ old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : '') }}">
                        @endswitch
                        @error($input['field'])
                            <div class="text-danger small mt-1">
                                {{ $message }}
                            </div>
                        @enderror
                    @endif
                </div>
            @endforeach
        </div>
        <div class="d-flex justify-content-end mt-4">
            <button class="btn btn-primary px-4" type="submit">
                {{ $isUpdate ? 'Guardar cambios' : 'Crear registro' }}
            </button>
        </div>
    </form>
</div>
