package me.hsgamer.votiful.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.data.Vote;
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
        plugin.get(VoteManager.class).getHolder().addVote(Vote.fromVotifier(event.getVote()));
    }
}
