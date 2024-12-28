package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.holder.VoteHolder;

public class VoteManager implements Loadable {
    private final Votiful plugin;
    private VoteHolder holder;

    public VoteManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public VoteHolder getHolder() {
        return holder;
    }

    @Override
    public void enable() {
        holder = new VoteHolder(plugin, plugin.get(MainConfig.class).getGroup());
        holder.register();
    }

    @Override
    public void disable() {
        holder.unregister();
    }
}
