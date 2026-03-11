package me.hsgamer.votiful.holder;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.agent.core.AgentHolder;
import me.hsgamer.topper.agent.core.DataEntryAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.data.core.DataEntry;
import me.hsgamer.topper.data.simple.SimpleDataHolder;
import me.hsgamer.topper.spigot.template.storagesupplier.SpigotStorageSupplierTemplate;
import me.hsgamer.topper.storage.core.DataStorage;
import me.hsgamer.topper.storage.sql.config.SqlDatabaseConfig;
import me.hsgamer.topper.storage.sql.core.SqlDatabaseSetting;
import me.hsgamer.topper.template.storagesupplier.StorageSupplierTemplate;
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
import java.util.Objects;

public class VoteHolder extends SimpleDataHolder<VoteKey, VoteValue> implements AgentHolder<VoteKey, VoteValue> {
    private final Votiful plugin;
    private final StorageAgent<VoteKey, VoteValue> storageAgent;
    private final VoteStatsAgent voteStatsAgent;
    private final VoteEventAgent voteEventAgent;
    private final List<Agent> agents;
    private final List<DataEntryAgent<VoteKey, VoteValue>> entryAgents;

    public VoteHolder(Votiful plugin) {
        this.plugin = plugin;

        this.agents = new ArrayList<>();
        this.entryAgents = new ArrayList<>();

        MainConfig mainConfig = plugin.get(MainConfig.class);

        DataStorage<VoteKey, VoteValue> storage = plugin.get(SpigotStorageSupplierTemplate.class)
                .getDataStorageSupplier(new StorageSupplierTemplate.Settings() {
                    @Override
                    public String storageType() {
                        return mainConfig.getStorageType();
                    }

                    @Override
                    public SqlDatabaseSetting databaseSetting() {
                        return new SqlDatabaseConfig("votiful", new BukkitConfig(plugin, "database.yml"));
                    }

                    @Override
                    public File baseFolder() {
                        return new File(plugin.getDataFolder(), "data");
                    }
                })
                .getStorage(mainConfig.getGroup(), VoteKey.FLAT_CONVERTER, VoteValue.FLAT_CONVERTER, VoteKey.SQL_CONVERTER, VoteValue.SQL_CONVERTER);
        storageAgent = new StorageAgent<VoteKey, VoteValue>(storage) {
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
