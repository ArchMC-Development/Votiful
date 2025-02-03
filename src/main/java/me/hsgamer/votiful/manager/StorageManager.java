package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.spigot.storage.simple.SpigotDataStorageBuilder;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.simple.builder.DataStorageBuilder;
import me.hsgamer.topper.storage.simple.config.DatabaseConfig;
import me.hsgamer.topper.storage.simple.converter.ValueConverter;
import me.hsgamer.topper.storage.simple.setting.DataStorageSetting;
import me.hsgamer.topper.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;

import java.io.File;

public class StorageManager implements Loadable {
    private final Votiful plugin;
    private DataStorageSupplier supplier;

    public StorageManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public DataStorageSupplier getSupplier() {
        return supplier;
    }

    public <K, V> DataStorage<K, V> buildStorage(String name, ValueConverter<K> keyConverter, ValueConverter<V> valueConverter) {
        return supplier.getStorage(name, keyConverter, valueConverter);
    }

    @Override
    public void enable() {
        DataStorageBuilder builder = new DataStorageBuilder();
        SpigotDataStorageBuilder.register(builder);

        supplier = builder.buildSupplier(
                plugin.get(MainConfig.class).getStorageType(),
                new DataStorageSetting() {
                    @Override
                    public DatabaseConfig getDatabaseSetting() {
                        return new DatabaseConfig("votiful", new BukkitConfig(plugin, "database.yml"));
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
