package me.hsgamer.votiful.builder;

import me.hsgamer.topper.agent.storage.simple.setting.DatabaseSetting;
import me.hsgamer.topper.spigot.agent.storage.simple.SpigotDataStorageBuilder;
import me.hsgamer.votiful.builder.converter.VoteEntryFlatConverter;
import me.hsgamer.votiful.builder.converter.VoteEntrySqlConverter;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.function.Supplier;

public class VoteStorageBuilder extends SpigotDataStorageBuilder<VoteKey, VoteValue> {
    public VoteStorageBuilder(JavaPlugin plugin, File holderBaseFolder, Supplier<DatabaseSetting> databaseSettingSupplier) {
        super(
                plugin,
                holderBaseFolder,
                databaseSettingSupplier,
                VoteEntryFlatConverter.INSTANCE,
                VoteEntrySqlConverter.INSTANCE
        );
    }
}
