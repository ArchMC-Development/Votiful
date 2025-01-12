package me.hsgamer.votiful.config.converter;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.annotation.converter.Converter;

import java.util.List;

public class StringListConverter implements Converter {
    @Override
    public List<String> convert(Object o) {
        return CollectionUtils.createStringListFromObject(o);
    }

    @Override
    public Object convertToRaw(Object o) {
        return o;
    }
}
