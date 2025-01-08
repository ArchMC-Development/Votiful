package me.hsgamer.votiful.agent;

import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.data.VoteTableDiffSnapshot;
import me.hsgamer.votiful.manager.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VoteEventAgent implements Agent, Runnable {
    private final Votiful plugin;
    private final Queue<VoteTableDiffSnapshot> voteTableDiffSnapshotQueue = new ConcurrentLinkedQueue<>();

    public VoteEventAgent(Votiful plugin) {
        this.plugin = plugin;
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
        List<String> commands = plugin.get(EventManager.class).handle(voteTableDiffSnapshot);
        GlobalScheduler.get(plugin).run(() -> {
            for (String command : commands) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
            }
        });
    }

    void triggerUpdate(VoteTableDiffSnapshot voteTableDiffSnapshot) {
        voteTableDiffSnapshotQueue.add(voteTableDiffSnapshot);
    }
}
