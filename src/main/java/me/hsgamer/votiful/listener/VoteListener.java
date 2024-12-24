package me.hsgamer.votiful.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.manager.VoteManager;
import org.bukkit.event.EventHandler;

public class VoteListener implements ListenerComponent {
    private final Votiful plugin;

    public VoteListener(Votiful plugin) {
        this.plugin = plugin;
    }

    @Override
    public Votiful getPlugin() {
        return plugin;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        plugin.get(VoteManager.class).getHolder()
                .getOrCreateEntry(new VoteKey(plugin.get(MainConfig.class).getServerName()))
                .setValue(VoteValue.fromVotifier(event.getVote()));
    }
}
