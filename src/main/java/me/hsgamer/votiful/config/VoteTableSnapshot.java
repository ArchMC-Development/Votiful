package me.hsgamer.votiful.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;

import java.util.Map;

public class VoteTableSnapshot {
    public final Table<String, String, Integer> voteTable; // row: player, column: server

    public VoteTableSnapshot(Table<String, String, Integer> voteTable) {
        this.voteTable = ImmutableTable.copyOf(voteTable);
    }

    public VoteTableSnapshot() {
        this(ImmutableTable.of());
    }

    public VoteTableSnapshot(Map<VoteKey, DataEntry<VoteKey, VoteValue>> entryMap) {
        Table<String, String, Integer> table = HashBasedTable.create();
        for (DataEntry<VoteKey, VoteValue> entry : entryMap.values()) {
            VoteKey key = entry.getKey();
            VoteValue value = entry.getValue();
            String serverName = key.serverName;
            String playerName = value.playerName;

            Integer voteCount = table.get(playerName, serverName);
            if (voteCount == null) {
                voteCount = 1;
            } else {
                voteCount++;
            }
            table.put(playerName, serverName, voteCount);
        }
        this.voteTable = ImmutableTable.copyOf(table);
    }

    public int getVoteCount(String playerName, String serverName) {
        Integer voteCount = voteTable.get(playerName, serverName);
        return voteCount == null ? 0 : voteCount;
    }

    public Map<String, Integer> getServerVote(String serverName) {
        return voteTable.column(serverName);
    }

    public Map<String, Integer> getPlayerVote(String serverName) {
        return voteTable.row(serverName);
    }

    public int getServerVoteCount(String serverName) {
        return getServerVote(serverName).values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getPlayerVoteCount(String playerName) {
        return getPlayerVote(playerName).values().stream().mapToInt(Integer::intValue).sum();
    }
}
