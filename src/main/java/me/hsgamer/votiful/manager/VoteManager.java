package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.topper.agent.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.builder.VoteStorageBuilder;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.holder.VoteHolder;

public class VoteManager implements Loadable {
    private final Votiful plugin;
    private DataStorageSupplier<VoteKey, VoteValue> supplier;
    private VoteHolder holder;

    public VoteManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public DataStorageSupplier<VoteKey, VoteValue> getSupplier() {
        return supplier;
    }

    public VoteHolder getHolder() {
        return holder;
    }

    @Override
    public void enable() {
        supplier = plugin.get(VoteStorageBuilder.class).buildSupplier(plugin.get(MainConfig.class).getStorageType());
        supplier.enable();

        holder = new VoteHolder(plugin, plugin.get(MainConfig.class).getGroup());
        holder.register();
    }

    @Override
    public void disable() {
        holder.unregister();

        supplier.disable();
    }
}
