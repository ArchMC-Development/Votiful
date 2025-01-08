package me.hsgamer.votiful.config.converter;

import me.hsgamer.hscore.common.MapUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class StringStringObjectMapConverter implements Converter {
    @Override
    public Map<String, Map<String, Object>> convert(Object raw) {
        if (raw instanceof Map) {
            Map<String, Map<String, Object>> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) raw).entrySet()) {
                MapUtils.castOptionalStringObjectMap(entry.getValue()).ifPresent(value -> map.put(Objects.toString(entry.getKey()), value));
            }
            return map;
        }
        return null;
    }

    @Override
    public Object convertToRaw(Object o) {
        return o;
    }
}
