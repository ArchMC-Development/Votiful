package me.hsgamer.votiful.data;

import com.google.common.collect.ImmutableMap;
import me.hsgamer.topper.core.DataEntry;

import java.util.Map;
import java.util.Objects;

public class VoteTableSnapshot {
    public final Map<VoteKey, VoteValue> entryMap;

    public VoteTableSnapshot() {
        this.entryMap = ImmutableMap.of();
    }

    public VoteTableSnapshot(Map<VoteKey, DataEntry<VoteKey, VoteValue>> entryMap) {
        ImmutableMap.Builder<VoteKey, VoteValue> builder = ImmutableMap.builder();
        entryMap.forEach((key, value) -> builder.put(key, value.getValue()));
        this.entryMap = builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteTableSnapshot that = (VoteTableSnapshot) o;
        return Objects.equals(entryMap, that.entryMap);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entryMap);
    }

    @Override
    public String toString() {
        return "VoteTableSnapshot{" +
                "entryMap=" + entryMap +
                '}';
    }
}
