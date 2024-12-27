package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.hscore.database.Setting;
import me.hsgamer.topper.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.topper.storage.simple.setting.DataStorageBuilderSetting;
import me.hsgamer.topper.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.DatabaseConfig;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.holder.VoteHolder;

import java.io.File;
import java.util.function.Consumer;

public class VoteManager implements Loadable {
    private final Votiful plugin;
    private DataStorageSupplier supplier;
    private VoteHolder holder;

    public VoteManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public DataStorageSupplier getSupplier() {
        return supplier;
    }

    public VoteHolder getHolder() {
        return holder;
    }

    @Override
    public void enable() {
        supplier = plugin.get(DataStorageBuilder.class).buildSupplier(
                plugin.get(MainConfig.class).getStorageType(),
                new DataStorageBuilderSetting() {
                    @Override
                    public Consumer<Setting> getDatabaseSettingModifier() {
                        return ConfigGenerator.newInstance(DatabaseConfig.class, new BukkitConfig(plugin, "database.yml"));
                    }

                    @Override
                    public File getBaseFolder() {
                        return new File(plugin.getDataFolder(), "data");
                    }
                }
        );
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
