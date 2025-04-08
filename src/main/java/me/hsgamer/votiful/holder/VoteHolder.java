package me.hsgamer.votiful.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.topper.agent.holder.AgentDataHolder;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.agent.VoteEventAgent;
import me.hsgamer.votiful.agent.VoteStatsAgent;
import me.hsgamer.votiful.agent.VoteSyncAgent;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.Vote;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.manager.StorageManager;

import java.util.Objects;

public class VoteHolder extends AgentDataHolder<VoteKey, VoteValue> {
    private final Votiful plugin;
    private final StorageAgent<VoteKey, VoteValue> storageAgent;
    private final VoteStatsAgent voteStatsAgent;
    private final VoteEventAgent voteEventAgent;

    public VoteHolder(Votiful plugin, String name) {
        super(name);
        this.plugin = plugin;

        MainConfig mainConfig = plugin.get(MainConfig.class);

        storageAgent = new StorageAgent<VoteKey, VoteValue>(this, plugin.get(StorageManager.class).buildStorage(name, VoteKey.CONVERTER, VoteValue.CONVERTER)) {
            @Override
            public void onUpdate(DataEntry<VoteKey, VoteValue> entry, VoteValue oldValue, VoteValue newValue) {
                VoteKey key = entry.getKey();
                if (Objects.equals(key.serverName, mainConfig.getServerName())) {
                    super.onUpdate(entry, oldValue, newValue);
                }
            }
        };
        storageAgent.setMaxEntryPerCall(mainConfig.getTasksSaveEntryPerCall());
        addAgent(storageAgent);
        addEntryAgent(storageAgent);
        addAgent(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), mainConfig.getTasksSaveInterval()));

        if (mainConfig.isTasksSyncEnable()) {
            addAgent(new SpigotRunnableAgent(new VoteSyncAgent(plugin, this), AsyncScheduler.get(plugin), mainConfig.getTasksSyncInterval()));
        }

        voteStatsAgent = new VoteStatsAgent(this);
        addAgent(voteStatsAgent);
        addEntryAgent(voteStatsAgent);
        addAgent(new SpigotRunnableAgent(voteStatsAgent, AsyncScheduler.get(plugin), mainConfig.getTasksStatsInterval()));

        voteEventAgent = new VoteEventAgent(plugin);
        addAgent(new SpigotRunnableAgent(voteEventAgent, AsyncScheduler.get(plugin), mainConfig.getTasksEventInterval()));
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
}
