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
                                @php
                                    $actual = old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : ($input['default'] ?? ''));
                                @endphp
                                <select class="form-select" id="{{ $input['field'] }}" name="{{ $input['field'] }}" required>
                                    <option value="" disabled @selected($actual === '' || $actual === null)>Seleccionar...</option>
                                    @foreach ($input['options'] as $value => $text)
                                        <option value="{{ $value }}" @selected((string) $actual !== '' && $actual !== null && (string) $actual === (string) $value)>
                                            {{ $text }}
                                        </option>
                                    @endforeach
                                </select>
                            @break

                            @case('textarea')
                                <textarea class="form-control {{ !empty($input['auto_grow']) ? 'js-auto-grow' : '' }}" id="{{ $input['field'] }}" name="{{ $input['field'] }}" rows="{{ $input['rows'] ?? 4 }}"
                                    placeholder="{{ $input['label'] }}"
                                    @if(!empty($input['maxlength'])) maxlength="{{ $input['maxlength'] }}" @endif
                                    @if(!empty($input['auto_grow'])) style="overflow:hidden; resize:none;" @endif>{{ old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : '') }}</textarea>
                            @break

                            @default
                                @if ($input['field'] === 'password')
                                    <input class="form-control" type="text" id="{{ $input['field'] }}"
                                        name="{{ $input['field'] }}" placeholder="{{ $input['label'] }}"
                                        value="{{ old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : '') }}">
                                    @break
                                @endif
                                <input class="form-control" type="{{ $input['type'] }}" id="{{ $input['field'] }}"
                                    name="{{ $input['field'] }}" placeholder="{{ $input['label'] }}"
                                    value="{{ old($input['field'], $isUpdate ? ${$parameter}?->{$input['field']} : '') }}"
                                    @if(isset($input['maxlength'])) maxlength="{{ $input['maxlength'] }}" @endif>
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

@once
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            function autoGrow(field) {
                field.style.height = 'auto';
                field.style.height = field.scrollHeight + 'px';
            }

            function initAutoGrowFields(root) {
                (root || document).querySelectorAll('.js-auto-grow').forEach(function (field) {
                    if (field.dataset.autoGrowBound === '1') {
                        autoGrow(field);
                        return;
                    }
                    field.dataset.autoGrowBound = '1';
                    field.addEventListener('input', function () {
                        autoGrow(field);
                    });
                    autoGrow(field);
                });
            }

            initAutoGrowFields(document);
        });
    </script>
@endonce
