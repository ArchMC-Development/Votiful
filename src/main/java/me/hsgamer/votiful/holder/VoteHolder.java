package me.hsgamer.votiful.holder;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.gson.GsonConfig;
import me.hsgamer.hscore.database.client.sql.java.JavaSqlClient;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.data.core.DataEntry;
import me.hsgamer.topper.data.simple.SimpleDataHolder;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.flat.configfile.ConfigFileDataStorage;
import me.hsgamer.topper.storage.flat.properties.PropertiesDataStorage;
import me.hsgamer.topper.storage.sql.config.SqlDatabaseConfig;
import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;
import me.hsgamer.topper.storage.sql.mysql.MySqlDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.NewSqliteDataStorageSupplier;
import me.hsgamer.topper.storage.sql.sqlite.SqliteDataStorageSupplier;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.agent.TaskRunAgent;
import me.hsgamer.votiful.agent.VoteEventAgent;
import me.hsgamer.votiful.agent.VoteStatsAgent;
import me.hsgamer.votiful.agent.VoteSyncAgent;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.Vote;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

public class VoteHolder extends SimpleDataHolder<VoteKey, VoteValue> implements AgentHolder<VoteKey, VoteValue> {
    private final Votiful plugin;
    private final String name;
    private final StorageAgent<VoteKey, VoteValue> storageAgent;
    private final VoteStatsAgent voteStatsAgent;
    private final VoteEventAgent voteEventAgent;
    private final List<Agent> agents;
    private final List<DataEntryAgent<VoteKey, VoteValue>> entryAgents;

    public VoteHolder(Votiful plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        this.agents = new ArrayList<>();
        this.entryAgents = new ArrayList<>();

        MainConfig mainConfig = plugin.get(MainConfig.class);

        storageAgent = new StorageAgent<VoteKey, VoteValue>(getStorage(mainConfig.getStorageType())) {
            @Override
            public void onUpdate(DataEntry<VoteKey, VoteValue> entry, VoteValue oldValue, VoteValue newValue) {
                VoteKey key = entry.getKey();
                if (Objects.equals(key.serverName, mainConfig.getServerName())) {
                    super.onUpdate(entry, oldValue, newValue);
                }
            }
        };
        storageAgent.setMaxEntryPerCall(mainConfig.getTasksSaveEntryPerCall());
        agents.add(storageAgent);
        entryAgents.add(storageAgent);
        agents.add(storageAgent.getLoadAgent(this));
        agents.add(new TaskRunAgent(plugin, storageAgent, mainConfig.getTasksSaveInterval()));
        if (mainConfig.isTasksSyncEnable()) {
            agents.add(new TaskRunAgent(plugin, new VoteSyncAgent(plugin, this), mainConfig.getTasksSyncInterval()));
        }

        voteStatsAgent = new VoteStatsAgent(this);
        agents.add(voteStatsAgent);
        entryAgents.add(voteStatsAgent);
        agents.add(new TaskRunAgent(plugin, voteStatsAgent, mainConfig.getTasksStatsInterval()));

        voteEventAgent = new VoteEventAgent(plugin);
        agents.add(new TaskRunAgent(plugin, voteEventAgent, mainConfig.getTasksEventInterval()));
    }

    private DataStorage<VoteKey, VoteValue> getStorage(String type) {
        Supplier<SqlDatabaseSetting> databaseSettingSupplier = () -> new SqlDatabaseConfig("votiful", new BukkitConfig(plugin, "database.yml"));
        Supplier<File> baseFolderSupplier = () -> new File(plugin.getDataFolder(), "data");
        switch (type.toLowerCase(Locale.ROOT)) {
            case "mysql":
                return new MySqlDataStorageSupplier(databaseSettingSupplier.get(), JavaSqlClient::new).getStorage(name, VoteKey.SQL_CONVERTER, VoteValue.SQL_CONVERTER);
            case "sqlite":
                return new SqliteDataStorageSupplier(baseFolderSupplier.get(), databaseSettingSupplier.get(), JavaSqlClient::new).getStorage(name, VoteKey.SQL_CONVERTER, VoteValue.SQL_CONVERTER);
            case "new-sqlite":
                return new NewSqliteDataStorageSupplier(baseFolderSupplier.get(), databaseSettingSupplier.get(), JavaSqlClient::new).getStorage(name, VoteKey.SQL_CONVERTER, VoteValue.SQL_CONVERTER);
            case "json":
                return new ConfigFileDataStorage<VoteKey, VoteValue>(baseFolderSupplier.get(), name, VoteKey.FLAT_CONVERTER, VoteValue.FLAT_CONVERTER) {
                    @Override
                    protected Config getConfig(File file) {
                        return new GsonConfig(file);
                    }

                    @Override
                    protected String getConfigName(String name) {
                        return name + ".json";
                    }
                };
            case "yaml":
                return new ConfigFileDataStorage<VoteKey, VoteValue>(baseFolderSupplier.get(), name, VoteKey.FLAT_CONVERTER, VoteValue.FLAT_CONVERTER) {
                    @Override
                    protected Config getConfig(File file) {
                        return new BukkitConfig(file);
                    }

                    @Override
                    protected String getConfigName(String name) {
                        return name + ".yml";
                    }
                };
            case "flat":
            default:
                return new PropertiesDataStorage<>(baseFolderSupplier.get(), name, VoteKey.FLAT_CONVERTER, VoteValue.FLAT_CONVERTER);
        }
    }

    @Override
    public VoteValue getDefaultValue() {
        return VoteValue.EMPTY;
    }

    public StorageAgent<VoteKey, VoteValue> getStorageAgent() {
        return storageAgent;
    }

    public VoteStatsAgent getVoteStatsAgent() {
        return voteStatsAgent;
    }

    public VoteEventAgent getVoteEventAgent() {
        return voteEventAgent;
    }

    public void addVote(Vote vote) {
        VoteKey key = new VoteKey(plugin.get(MainConfig.class).getServerName(), vote.playerName, vote.serviceName);
        getOrCreateEntry(key).setValue(entry -> entry.addVote(vote.timestamp));
    }

    @Override
    public List<Agent> getAgents() {
        return agents;
    }

    @Override
    public List<DataEntryAgent<VoteKey, VoteValue>> getEntryAgents() {
        return entryAgents;
    }
}
