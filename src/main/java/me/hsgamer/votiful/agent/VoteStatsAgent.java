package me.hsgamer.votiful.agent;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.holder.VoteHolder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VoteStatsAgent implements Agent<VoteKey, VoteValue>, Runnable {
    private final VoteHolder holder;
    private final AtomicBoolean needUpdate = new AtomicBoolean(true);
    private final AtomicReference<Table<String, String, Integer>> playerVotePerServerRef = new AtomicReference<>(ImmutableTable.of()); // row: player, column: server

    public VoteStatsAgent(VoteHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        if (!needUpdate.getAndSet(false)) return;

        Map<VoteKey, DataEntry<VoteKey, VoteValue>> voteMap = holder.getEntryMap();

        Table<String, String, Integer> playerVotePerServer = HashBasedTable.create();
        for (DataEntry<VoteKey, VoteValue> entry : voteMap.values()) {
            VoteKey key = entry.getKey();
            VoteValue value = entry.getValue();
            String serverName = key.serverName;
            String playerName = value.playerName;

            Integer voteCount = playerVotePerServer.get(playerName, serverName);
            if (voteCount == null) {
                voteCount = 1;
            } else {
                voteCount++;
            }
            playerVotePerServer.put(playerName, serverName, voteCount);
        }
        playerVotePerServerRef.set(ImmutableTable.copyOf(playerVotePerServer));
    }

    public int getVoteCount(String playerName, String serverName) {
        Integer voteCount = playerVotePerServerRef.get().get(playerName, serverName);
        return voteCount == null ? 0 : voteCount;
    }

    public Map<String, Integer> getServerVote(String serverName) {
        return playerVotePerServerRef.get().column(serverName);
    }

    public Map<String, Integer> getPlayerVote(String serverName) {
        return playerVotePerServerRef.get().row(serverName);
    }

    public int getServerVoteCount(String serverName) {
        return getServerVote(serverName).values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getPlayerVoteCount(String playerName) {
        return getPlayerVote(playerName).values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public void onCreate(DataEntry<VoteKey, VoteValue> entry) {
        needUpdate.lazySet(true);
    }

    @Override
    public void onUpdate(DataEntry<VoteKey, VoteValue> entry) {
        needUpdate.lazySet(true);
    }

    @Override
    public void onRemove(DataEntry<VoteKey, VoteValue> entry) {
        needUpdate.lazySet(true);
    }
}
