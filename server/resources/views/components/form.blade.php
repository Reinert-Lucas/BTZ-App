{{-- Solo Dios sabe como se concatena cada string asi que ni me preguntes --}}

@if ($ruta === "update")
    @php
        $id = ${$parameter}->{$parameter . "_id"}
    @endphp
    <form method="POST" action={{ route("admin.{$resource}.{$ruta}", ["{$parameter}" => "{$id}"]) }}>
        @csrf
        @method($method)
        @foreach ($inputs as $input)
            <label for="{{ $input['field'] }}">{{ $input['label'] }}</label>
            @switch($input['type'])
                @case('select')
                    <select
                        id="{{ $input['field'] }}"
                        name="{{ $input['field'] }}">
                        @foreach($input['options'] as $value => $text)
                            <option
                                value="{{ $value }}"
                                @selected(old($input['field'], ${$parameter}?->{$input['field']}) == $value)>
                                {{ $text }}
                            </option>
                        @endforeach
                    </select>
                    @break
                @case('textarea')
                    <textarea 
                    name="{{ $input['field'] }}"
                    id="{{ $input['field'] }}">{{ ${$parameter}->{$input['field']} }}</textarea>
                    @break
                @case('checkbox')
                    <input
                    type="checkbox" 
                    name="{{ $input['field'] }}"
                    id="{{ $input['field'] }}">    
                        {{ ${$parameter}->{$input['field']} }}
                    </input>
                    @break
                @default
                    <input 
                    type="{{ $input['type'] }}"
                    placeholder="{{ $input['label'] }}"
                    id="{{ $input['field'] }}"
                    name="{{ $input['field'] }}" 
                    value="{{ ${$parameter}->{$input['field']} }}">
                    @break
            @endswitch
        @endforeach
        <input type="submit" value="Enviar Formulario">
    </form>
@else
    <form method="POST" action={{ route("admin.{$resource}.{$ruta}") }}>
        @csrf
        @method($method)
        @foreach ($inputs as $input)
            <label for="{{ $input['field'] }}">{{ $input['label'] }}</label>
            @switch($input['type'])
                @case('select')
                    <select
                        id="{{ $input['field'] }}"
                        name="{{ $input['field'] }}">
                        @foreach($input['options'] as $value => $text)
                            <option
                                value="{{ $value }}"
                                @selected('operario')>
                                {{ $text }}
                            </option>
                        @endforeach
                    </select>
                    @break
                @case('textarea')
                    <textarea 
                    name="{{ $input['field'] }}"
                    id="{{ $input['field'] }}"
                    placeholder="{{ $input['label'] }}"></textarea>
                    @break
                @default
                    <input 
                    type="{{ $input['type'] }}"
                    placeholder="{{ $input['label'] }}"
                    id="{{ $input['field'] }}"
                    name="{{ $input['field'] }}">
                    @break
            @endswitch
        @endforeach
        <input type="submit" value="Enviar Formulario">
    </form>
@endif