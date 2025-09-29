package me.hsgamer.votiful.agent;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.votiful.Votiful;

public class TaskRunAgent implements Agent {
    private final Votiful plugin;
    private final Runnable runnable;
    private final long interval;
    private Task task;

    public TaskRunAgent(Votiful plugin, Runnable runnable, long interval) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.interval = interval;
    }

    @Override
    public void start() {
        task = AsyncScheduler.get(plugin).runTimer(runnable, interval, interval);
    }

    @Override
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
