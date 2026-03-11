package me.hsgamer.votiful;

import io.github.projectunified.minelib.plugin.base.BasePlugin;
import io.github.projectunified.minelib.plugin.command.CommandComponent;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.hscore.database.Setting;
import me.hsgamer.hscore.database.client.sql.SqlClient;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.spigot.template.storagesupplier.SpigotStorageSupplierTemplate;
import me.hsgamer.votiful.command.StressVoteCommand;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.hook.PlaceholderAPIHook;
import me.hsgamer.votiful.listener.VoteListener;
import me.hsgamer.votiful.manager.EventManager;
import me.hsgamer.votiful.manager.VoteManager;
import org.bstats.bukkit.Metrics;

import java.util.Arrays;
import java.util.List;

public final class Votiful extends BasePlugin {
    @Override
    protected List<Object> getComponents() {
        return Arrays.asList(
                ConfigGenerator.newInstance(MainConfig.class, new BukkitConfig(this)),

                new SpigotStorageSupplierTemplate() {
                    @Override
                    public SqlClient<?> getSqlClient(Setting setting) {
                        return new JavaSqlClient(setting);
                    }
                },

                new VoteManager(this),
                new EventManager(this),

                new VoteListener(this),
                new CommandComponent(this, new StressVoteCommand(this)),

                new PlaceholderAPIHook(this)
        );
    }

    @Override
    public void enable() {
        new Metrics(this, 25520);
    }
}
