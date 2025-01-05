package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.hscore.database.Setting;
import me.hsgamer.topper.spigot.storage.simple.SpigotDataStorageBuilder;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.topper.storage.simple.setting.DataStorageBuilderSetting;
import me.hsgamer.topper.storage.simple.setting.DataStorageSetting;
import me.hsgamer.topper.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.DatabaseConfig;
import me.hsgamer.votiful.config.MainConfig;

import java.io.File;
import java.util.function.Consumer;

public class StorageManager implements Loadable {
    private final Votiful plugin;
    private DataStorageSupplier supplier;

    public StorageManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public DataStorageSupplier getSupplier() {
        return supplier;
    }

    public <K, V> DataStorage<K, V> buildStorage(String name, DataStorageSetting<K, V> setting) {
        return supplier.getStorage(name, setting);
    }

    @Override
    public void enable() {
        DataStorageBuilder builder = new DataStorageBuilder();
        SpigotDataStorageBuilder.register(builder);

        supplier = builder.buildSupplier(
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
    }

    @Override
    public void disable() {
        if (supplier != null) {
            supplier.disable();
        }
    }
}
