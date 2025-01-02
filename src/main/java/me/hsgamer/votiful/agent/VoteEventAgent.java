package me.hsgamer.votiful.agent;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.votiful.config.VoteTableDiffSnapshot;
import me.hsgamer.votiful.config.VoteTableSnapshot;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.holder.VoteHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class VoteEventAgent implements Agent<VoteKey, VoteValue>, Runnable {
    private final VoteHolder holder;
    private final AtomicReference<VoteTableSnapshot> voteTableSnapshotRef = new AtomicReference<>(new VoteTableSnapshot());
    private final Queue<VoteTableDiffSnapshot> voteTableDiffSnapshotQueue = new ConcurrentLinkedQueue<>();

    public VoteEventAgent(VoteHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        List<VoteTableDiffSnapshot> voteTableDiffSnapshots = new ArrayList<>();
        while (true) {
            VoteTableDiffSnapshot voteTableDiffSnapshot = voteTableDiffSnapshotQueue.poll();
            if (voteTableDiffSnapshot == null) {
                break;
            }
            voteTableDiffSnapshots.add(voteTableDiffSnapshot);
        }

        if (voteTableDiffSnapshots.isEmpty()) {
            return;
        }

        VoteTableDiffSnapshot voteTableDiffSnapshot = new VoteTableDiffSnapshot(voteTableDiffSnapshots);
        // TODO: Implement the rest of the method
        System.out.println("Triggered update: " + voteTableDiffSnapshot.timestamp);
    }

    @Override
    public void start() {
        voteTableSnapshotRef.set(holder.getVoteStatsAgent().getVoteTableSnapshot());
    }

    void triggerUpdate() {
        VoteTableSnapshot oldSnapshot = voteTableSnapshotRef.get();
        VoteTableSnapshot newSnapshot = holder.getVoteStatsAgent().getVoteTableSnapshot();
        voteTableSnapshotRef.set(newSnapshot);
        voteTableDiffSnapshotQueue.add(new VoteTableDiffSnapshot(oldSnapshot, newSnapshot));
    }
}
