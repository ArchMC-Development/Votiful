package me.hsgamer.votiful.agent;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.votiful.data.VoteTableDiffSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoteEventAgent implements Agent, Runnable {
    private final Queue<VoteTableDiffSnapshot> voteTableDiffSnapshotQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void run() {
        List<VoteTableDiffSnapshot> voteTableDiffSnapshots = new ArrayList<>();
        while (true) {
            VoteTableDiffSnapshot voteTableDiffSnapshot = voteTableDiffSnapshotQueue.poll();
            if (voteTableDiffSnapshot == null) {
                break;
            }
            System.out.println("Polled: " + voteTableDiffSnapshot);
            voteTableDiffSnapshots.add(voteTableDiffSnapshot);
        }

        if (voteTableDiffSnapshots.isEmpty()) {
            return;
        }

        VoteTableDiffSnapshot voteTableDiffSnapshot = new VoteTableDiffSnapshot(voteTableDiffSnapshots);
        // TODO: Implement the rest of the method
        System.out.println("Triggered update: " + voteTableDiffSnapshot);
    }

    void triggerUpdate(VoteTableDiffSnapshot voteTableDiffSnapshot) {
        voteTableDiffSnapshotQueue.add(voteTableDiffSnapshot);
    }
}
