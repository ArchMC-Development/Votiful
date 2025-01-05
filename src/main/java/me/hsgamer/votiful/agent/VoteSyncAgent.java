package me.hsgamer.votiful.agent;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.holder.VoteHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoteSyncAgent implements Agent, Runnable {
    private final Votiful plugin;
    private final VoteHolder holder;

    public VoteSyncAgent(Votiful plugin, VoteHolder holder) {
        this.plugin = plugin;
        this.holder = holder;
    }

    @Override
    public void run() {
        Map<VoteKey, VoteValue> storageMap = holder.getStorageAgent().getStorage().load();
        String serverName = plugin.get(MainConfig.class).getServerName();

        List<VoteKey> toRemove = new ArrayList<>();
        for (VoteKey key : holder.getEntryMap().keySet()) {
            if (!key.serverName.equals(serverName) && !storageMap.containsKey(key)) {
                toRemove.add(key);
            }
        }

        for (VoteKey key : toRemove) {
            holder.removeEntry(key);
        }

        for (Map.Entry<VoteKey, VoteValue> entry : storageMap.entrySet()) {
            if (!entry.getKey().serverName.equals(serverName)) {
                holder.getOrCreateEntry(entry.getKey()).setValue(entry.getValue());
            }
        }
    }
}
