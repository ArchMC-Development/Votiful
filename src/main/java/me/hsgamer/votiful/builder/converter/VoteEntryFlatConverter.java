package me.hsgamer.votiful.builder.converter;

import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;

public class VoteEntryFlatConverter implements FlatEntryConverter<VoteKey, VoteValue> {
    public static final VoteEntryFlatConverter INSTANCE = new VoteEntryFlatConverter();

    private VoteEntryFlatConverter() {
    }

    @Override
    public VoteKey toKey(String key) {
        return VoteKey.fromString(key);
    }

    @Override
    public String toRawKey(VoteKey key) {
        return key.toString();
    }

    @Override
    public VoteValue toValue(String value) {
        return VoteValue.fromString(value);
    }

    @Override
    public String toRawValue(VoteValue object) {
        return object.toString();
    }
}
