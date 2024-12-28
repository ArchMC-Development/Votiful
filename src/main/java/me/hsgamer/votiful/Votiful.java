package me.hsgamer.votiful;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.votiful.command.StressVoteCommand;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.listener.VoteListener;
import me.hsgamer.votiful.manager.StorageManager;
import me.hsgamer.votiful.manager.VoteManager;

import java.util.Arrays;
import java.util.List;

public final class Votiful extends BasePlugin {
    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),

                new StorageManager(this),
                new VoteManager(this),

                new VoteListener(this),
                new CommandComponent(this, new StressVoteCommand(this))
        );
    }
}
