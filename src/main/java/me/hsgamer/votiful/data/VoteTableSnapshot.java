package me.hsgamer.votiful.data;

import com.google.common.collect.ImmutableMap;
import me.hsgamer.topper.data.core.DataEntry;

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
        int totalVotesValue = 0;
        Map<String, ImmutableMap.Builder<VoteKey, VoteValue>> serverMapTmp = new HashMap<>();
        Map<String, ImmutableMap.Builder<VoteKey, VoteValue>> playerMapTmp = new HashMap<>();
        Map<String, ImmutableMap.Builder<VoteKey, VoteValue>> serviceMapTmp = new HashMap<>();
        for (Map.Entry<VoteKey, DataEntry<VoteKey, VoteValue>> entry : entryMap.entrySet()) {
            VoteKey key = entry.getKey();
            VoteValue value = entry.getValue().getValue();
            builder.put(key, value);
            totalVotesValue += value.vote;

            serverMapTmp.computeIfAbsent(key.serverName, k -> ImmutableMap.builder()).put(key, value);
            playerMapTmp.computeIfAbsent(key.playerName, k -> ImmutableMap.builder()).put(key, value);
            serviceMapTmp.computeIfAbsent(key.serviceName, k -> ImmutableMap.builder()).put(key, value);
        }

        this.entryMap = builder.build();
        this.totalVotes = totalVotesValue;

        ImmutableMap.Builder<String, Map<VoteKey, VoteValue>> serverMapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, ImmutableMap.Builder<VoteKey, VoteValue>> entry : serverMapTmp.entrySet()) {
            serverMapBuilder.put(entry.getKey(), entry.getValue().build());
        }
        this.serverMap = serverMapBuilder.build();

        ImmutableMap.Builder<String, Map<VoteKey, VoteValue>> playerMapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, ImmutableMap.Builder<VoteKey, VoteValue>> entry : playerMapTmp.entrySet()) {
            playerMapBuilder.put(entry.getKey(), entry.getValue().build());
        }
        this.playerMap = playerMapBuilder.build();

        ImmutableMap.Builder<String, Map<VoteKey, VoteValue>> serviceMapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, ImmutableMap.Builder<VoteKey, VoteValue>> entry : serviceMapTmp.entrySet()) {
            serviceMapBuilder.put(entry.getKey(), entry.getValue().build());
        }
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
        if (this == o) return true;
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
