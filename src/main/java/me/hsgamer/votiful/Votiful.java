package me.hsgamer.votiful;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.votiful.builder.VoteStorageBuilder;
import me.hsgamer.votiful.config.DatabaseConfig;
import me.hsgamer.votiful.listener.VoteListener;
import me.hsgamer.votiful.manager.VoteManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Votiful extends BasePlugin {
    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                new VoteStorageBuilder(
                        this,
                        new File(getDataFolder(), "data"),
                        () -> ConfigGenerator.newInstance(DatabaseConfig.class, new BukkitConfig(this, "database.yml")).toDatabaseSetting()
                ),

                new VoteManager(this),

                new VoteListener(this)
        );
    }
}
