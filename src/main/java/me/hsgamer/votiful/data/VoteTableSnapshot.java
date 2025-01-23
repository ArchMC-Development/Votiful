package me.hsgamer.votiful.data;

import com.google.common.collect.ImmutableMap;
import me.hsgamer.topper.core.DataEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VoteTableSnapshot {
    public final Map<VoteKey, VoteValue> entryMap;
    public final int totalVotes;
    public final Map<String, Map<VoteKey, VoteValue>> serverMap;
    public final Map<String, Map<VoteKey, VoteValue>> playerMap;
    public final Map<String, Map<VoteKey, VoteValue>> serviceMap;

    public VoteTableSnapshot() {
        this.entryMap = ImmutableMap.of();
        this.totalVotes = 0;
        this.serverMap = ImmutableMap.of();
        this.playerMap = ImmutableMap.of();
        this.serviceMap = ImmutableMap.of();
    }

    public VoteTableSnapshot(Map<VoteKey, DataEntry<VoteKey, VoteValue>> entryMap) {
        ImmutableMap.Builder<VoteKey, VoteValue> builder = ImmutableMap.builder();
        int totalVotes = 0;
        Map<String, ImmutableMap.Builder<VoteKey, VoteValue>> serverMap = new HashMap<>();
        Map<String, ImmutableMap.Builder<VoteKey, VoteValue>> playerMap = new HashMap<>();
        Map<String, ImmutableMap.Builder<VoteKey, VoteValue>> serviceMap = new HashMap<>();
        for (Map.Entry<VoteKey, DataEntry<VoteKey, VoteValue>> entry : entryMap.entrySet()) {
            VoteKey key = entry.getKey();
            VoteValue value = entry.getValue().getValue();
            builder.put(key, value);
            totalVotes += value.vote;

            serverMap.computeIfAbsent(key.serverName, k -> ImmutableMap.builder()).put(key, value);
            playerMap.computeIfAbsent(key.playerName, k -> ImmutableMap.builder()).put(key, value);
            serviceMap.computeIfAbsent(key.serviceName, k -> ImmutableMap.builder()).put(key, value);
        }

        this.entryMap = builder.build();
        this.totalVotes = totalVotes;

        ImmutableMap.Builder<String, Map<VoteKey, VoteValue>> serverMapBuilder = ImmutableMap.builder();
        serverMap.forEach((key, value) -> serverMapBuilder.put(key, value.build()));
        this.serverMap = serverMapBuilder.build();

        ImmutableMap.Builder<String, Map<VoteKey, VoteValue>> playerMapBuilder = ImmutableMap.builder();
        playerMap.forEach((key, value) -> playerMapBuilder.put(key, value.build()));
        this.playerMap = playerMapBuilder.build();

        ImmutableMap.Builder<String, Map<VoteKey, VoteValue>> serviceMapBuilder = ImmutableMap.builder();
        serviceMap.forEach((key, value) -> serviceMapBuilder.put(key, value.build()));
        this.serviceMap = serviceMapBuilder.build();
    }

    public Map<VoteKey, VoteValue> serverMap(String serverName) {
        return serverMap.getOrDefault(serverName, ImmutableMap.of());
    }

    public Map<VoteKey, VoteValue> playerMap(String playerName) {
        return playerMap.getOrDefault(playerName, ImmutableMap.of());
    }

    public Map<VoteKey, VoteValue> serviceMap(String serviceName) {
        return serviceMap.getOrDefault(serviceName, ImmutableMap.of());
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
