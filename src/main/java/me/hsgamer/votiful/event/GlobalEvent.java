package me.hsgamer.votiful.event;

import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteTableDiffSnapshot;
import me.hsgamer.votiful.data.VoteValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GlobalEvent implements Event {
    private final KeyType keyType;
    private final Predicate<VoteKey> voteKeyPredicate;
    private final int voteValue;
    private final boolean repeat;
    private final List<String> commands;

    private GlobalEvent(KeyType keyType, Predicate<VoteKey> voteKeyPredicate, int voteValue, boolean repeat, List<String> commands) {
        this.keyType = keyType;
        this.voteKeyPredicate = voteKeyPredicate;
        this.voteValue = voteValue;
        this.repeat = repeat;
        this.commands = commands;
    }

    public static GlobalEvent create(Map<String, Object> map) {
        KeyType keyType;
        try {
            keyType = KeyType.valueOf(Objects.toString(map.get("key-type")).toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid key type: " + map.get("key-type"));
        }

        Predicate<VoteKey> voteKeyPredicate = key -> true;

        List<String> serviceRules = CollectionUtils.createStringListFromObject(map.get("service-rule"), true);
        if (!serviceRules.isEmpty()) {
            Predicate<VoteKey> servicePredicate = key -> serviceRules.contains(key.serviceName);
            voteKeyPredicate = voteKeyPredicate.and(servicePredicate);
        }

        List<String> playerRules = CollectionUtils.createStringListFromObject(map.get("player-rule"), true);
        if (!playerRules.isEmpty()) {
            Predicate<VoteKey> playerPredicate = key -> playerRules.contains(key.playerName);
            voteKeyPredicate = voteKeyPredicate.and(playerPredicate);
        }

        List<String> serverRules = CollectionUtils.createStringListFromObject(map.get("server-rule"), true);
        if (!serverRules.isEmpty()) {
            boolean hasCurrentServer = serverRules.contains("@current");
            Predicate<VoteKey> serverPredicate = key -> {
                if (hasCurrentServer && key.serverName.equals(JavaPlugin.getPlugin(Votiful.class).get(MainConfig.class).getServerName())) {
                    return true;
                }
                return serverRules.contains(key.serverName);
            };
            voteKeyPredicate = voteKeyPredicate.and(serverPredicate);
        }

        int voteValue;
        try {
            voteValue = Integer.parseInt(Objects.toString(map.get("value")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid vote value: " + map.get("value"));
        }

        boolean repeat = Boolean.parseBoolean(Objects.toString(map.get("repeat")));

        List<String> commands = CollectionUtils.createStringListFromObject(map.get("commands"), true);

        return new GlobalEvent(keyType, voteKeyPredicate, voteValue, repeat, commands);
    }

    private Map<String, Integer> getVoteMap(Map<VoteKey, VoteValue> entryMap, KeyType keyType) {
        return entryMap.entrySet()
                .stream()
                .filter(entry -> voteKeyPredicate.test(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> {
                            switch (keyType) {
                                case GROUP:
                                    return JavaPlugin.getPlugin(Votiful.class).get(MainConfig.class).getGroup();
                                case SERVICE:
                                    return entry.getKey().serviceName;
                                case PLAYER:
                                    return entry.getKey().playerName;
                                case SERVER:
                                    return entry.getKey().serverName;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + keyType);
                            }
                        },
                        entry -> entry.getValue().vote,
                        Integer::sum
                ));
    }

    @Override
    public List<String> handle(VoteTableDiffSnapshot snapshot) {
        Map<String, Integer> voteMap = getVoteMap(snapshot.newSnapshot.entryMap, keyType);

        Map<String, Integer> startMap = new HashMap<>();
        if (repeat) {
            Map<String, Integer> oldVoteMap = getVoteMap(snapshot.oldSnapshot.entryMap, keyType);
            for (Map.Entry<String, Integer> entry : voteMap.entrySet()) {
                int vote = entry.getValue();
                int oldVote = oldVoteMap.getOrDefault(entry.getKey(), 0);
                int voteThreshold = ((oldVote / voteValue) + 1) * voteValue;
                if (vote >= voteThreshold && oldVote < voteThreshold) {
                    startMap.put(entry.getKey(), vote);
                }
            }
        } else {
            for (Map.Entry<String, Integer> entry : voteMap.entrySet()) {
                int vote = entry.getValue();
                if (vote >= voteValue) {
                    startMap.put(entry.getKey(), vote);
                }
            }
        }

        return startMap.entrySet()
                .stream()
                .map(entry -> commands.stream()
                        .map(s -> s
                                .replace("{key}", entry.getKey())
                                .replace("{value}", String.valueOf(entry.getValue())))
                        .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public enum KeyType {
        SERVER,
        GROUP,
        SERVICE,
        PLAYER
    }
}
