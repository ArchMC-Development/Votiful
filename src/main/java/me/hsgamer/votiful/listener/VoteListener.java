package me.hsgamer.votiful.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import io.github.projectunified.minelib.plugin.listener.ListenerComponent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.Vote;
import me.hsgamer.votiful.manager.VoteManager;
import org.bukkit.Bukkit;
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

    private boolean isVoteAllowed(Vote vote) {
        MainConfig config = plugin.get(MainConfig.class);
        return (config.isVotesOffline() || Bukkit.getPlayer(vote.playerName) != null) && config.isVoteServiceEnabled(vote.serviceName);
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        Vote vote = Vote.fromVotifier(event.getVote());
        if (isVoteAllowed(vote)) {
            plugin.get(VoteManager.class).getHolder().addVote(vote);
        }
    }
}
