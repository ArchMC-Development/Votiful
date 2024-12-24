package me.hsgamer.votiful.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.topper.agent.holder.AgentDataHolder;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.agent.VoteSyncAgent;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.manager.VoteManager;

public class VoteHolder extends AgentDataHolder<VoteKey, VoteValue> {
    private final StorageAgent<VoteKey, VoteValue> storageAgent;

    public VoteHolder(Votiful plugin, String name) {
        super(name);

        storageAgent = new StorageAgent<>(plugin.getLogger(), this, plugin.get(VoteManager.class).getSupplier().getStorage(name));
        addAgent(new SpigotRunnableAgent<>(storageAgent, AsyncScheduler.get(plugin), 10));

        VoteSyncAgent voteSyncAgent = new VoteSyncAgent(this);
        addAgent(new SpigotRunnableAgent<>(voteSyncAgent, AsyncScheduler.get(plugin), 10));
    }

    public StorageAgent<VoteKey, VoteValue> getStorageAgent() {
        return storageAgent;
    }
}
