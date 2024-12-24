package me.hsgamer.votiful.agent;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.holder.VoteHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoteSyncAgent implements Agent<VoteKey, VoteValue>, Runnable {
    private final VoteHolder holder;

    public VoteSyncAgent(VoteHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        Map<VoteKey, VoteValue> storageMap = holder.getStorageAgent().getStorage().load();

        List<VoteKey> toRemove = new ArrayList<>();
        for (VoteKey key : holder.getEntryMap().keySet()) {
            if (!storageMap.containsKey(key)) {
                toRemove.add(key);
            }
        }

        for (VoteKey key : toRemove) {
            holder.getEntryMap().remove(key);
        }

        for (Map.Entry<VoteKey, VoteValue> entry : storageMap.entrySet()) {
            holder.getOrCreateEntry(entry.getKey()).setValue(entry.getValue());
        }
    }
}
