package me.hsgamer.votiful;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import me.hsgamer.topper.spigot.storage.simple.SpigotDataStorageBuilder;
import me.hsgamer.topper.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.votiful.listener.VoteListener;
import me.hsgamer.votiful.manager.VoteManager;

import java.util.Arrays;
import java.util.List;

public final class Votiful extends BasePlugin {
    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                new DataStorageBuilder(),

                new VoteManager(this),

                new VoteListener(this)
        );
    }

    @Override
    public void load() {
        SpigotDataStorageBuilder.register(this, get(DataStorageBuilder.class));
    }
}
