package me.hsgamer.votiful.agent;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.votiful.data.VoteTableDiffSnapshot;
import me.hsgamer.votiful.data.VoteTableSnapshot;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.holder.VoteHolder;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VoteStatsAgent implements Agent<VoteKey, VoteValue>, Runnable {
    private final VoteHolder holder;
    private final AtomicBoolean needUpdate = new AtomicBoolean(false);
    private final AtomicReference<VoteTableSnapshot> voteTableSnapshotRef = new AtomicReference<>(new VoteTableSnapshot());

    public VoteStatsAgent(VoteHolder holder) {
        this.holder = holder;
    }

    private void updateStats(boolean triggerUpdate) {
        VoteTableSnapshot oldSnapshot = voteTableSnapshotRef.get();
        VoteTableSnapshot newSnapshot = new VoteTableSnapshot(holder.getEntryMap());
        voteTableSnapshotRef.set(newSnapshot);
        if (triggerUpdate) {
            holder.getVoteEventAgent().triggerUpdate(new VoteTableDiffSnapshot(oldSnapshot, newSnapshot));
        }
    }

    @Override
    public void run() {
        if (!needUpdate.getAndSet(false)) return;
        updateStats(true);
    }

    @Override
    public void start() {
        updateStats(false);
    }

    public VoteTableSnapshot getVoteTableSnapshot() {
        return voteTableSnapshotRef.get();
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
