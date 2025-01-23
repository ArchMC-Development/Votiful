package me.hsgamer.votiful.hook.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteTableSnapshot;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.manager.VoteManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class VoteExpansion extends PlaceholderExpansion {
    private final Votiful plugin;

    public VoteExpansion(Votiful plugin) {
        this.plugin = plugin;
    }

    private static int countVotes(Map<VoteKey, VoteValue> map, Predicate<VoteKey> filter) {
        return map.entrySet().stream()
                .filter(entry -> filter.test(entry.getKey()))
                .mapToInt(entry -> entry.getValue().vote)
                .sum();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "votiful";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] split = params.split(Pattern.quote(";"));
        if (split.length == 0) {
            return null;
        }

        VoteTableSnapshot voteTableSnapshot = plugin.get(VoteManager.class).getHolder().getVoteStatsAgent().getVoteTableSnapshot();

        Map<VoteKey, VoteValue> voteMap;
        List<String> filters;
        String type = split[0];
        if (type.equalsIgnoreCase("group")) {
            voteMap = voteTableSnapshot.entryMap;
            filters = Arrays.asList(split).subList(1, split.length);
        } else if (type.equalsIgnoreCase("server")) {
            if (split.length < 2) {
                return null;
            }
            String name = split[1];
            if (name.equalsIgnoreCase("@current")) {
                name = plugin.get(MainConfig.class).getServerName();
            }
            voteMap = voteTableSnapshot.serverMap(name);
            filters = Arrays.asList(split).subList(2, split.length);
        } else if (type.equalsIgnoreCase("service")) {
            if (split.length < 2) {
                return null;
            }
            voteMap = voteTableSnapshot.serviceMap(split[1]);
            filters = Arrays.asList(split).subList(2, split.length);
        } else if (type.equalsIgnoreCase("player")) {
            String name = player.getName();
            if (name == null) {
                return null;
            }
            voteMap = voteTableSnapshot.playerMap(name);
            filters = Arrays.asList(split).subList(1, split.length);
        } else {
            return null;
        }

        List<String> serverFilters = new ArrayList<>();
        List<String> serviceFilters = new ArrayList<>();
        List<String> playerFilters = new ArrayList<>();
        for (String filter : filters) {
            String[] filterSplit = filter.split(Pattern.quote("="), 2);
            if (filterSplit.length != 2) {
                return null;
            }
            String key = filterSplit[0];
            String value = filterSplit[1];
            if (key.equalsIgnoreCase("server")) {
                serverFilters.add(value);
            } else if (key.equalsIgnoreCase("service")) {
                serviceFilters.add(value);
            } else if (key.equalsIgnoreCase("player")) {
                playerFilters.add(value);
            }
        }

        Predicate<VoteKey> filter = voteKey -> true;
        if (!serverFilters.isEmpty()) {
            filter = filter.and(voteKey -> serverFilters.contains(voteKey.serverName));
        }
        if (!serviceFilters.isEmpty()) {
            filter = filter.and(voteKey -> serviceFilters.contains(voteKey.serviceName));
        }
        if (!playerFilters.isEmpty()) {
            filter = filter.and(voteKey -> playerFilters.contains(voteKey.playerName));
        }

        return String.valueOf(countVotes(voteMap, filter));
    }
}
