package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.Vote;
import me.hsgamer.votiful.holder.VoteHolder;
import org.bukkit.Bukkit;

import java.util.*;

public class VoteManager implements Loadable {
    private final Votiful plugin;
    private VoteHolder holder;

    private boolean acceptOfflineVote;
    private boolean acceptAnyServices;
    private Set<String> acceptedServices = Collections.emptySet();

    public VoteManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public VoteHolder getHolder() {
        return holder;
    }

    @Override
    public void enable() {
        setupConfig();

        holder = new VoteHolder(plugin);
        holder.register();
    }

    private void setupConfig() {
        MainConfig config = plugin.get(MainConfig.class);

        acceptOfflineVote = config.isVotesOffline();

        List<String> services = config.getVoteServices();
        acceptedServices = new HashSet<>();
        for (String service : services) {
            if (service.equals("*")) {
                acceptAnyServices = true;
                break;
            } else {
                acceptAnyServices = false;
                acceptedServices.add(service.toLowerCase(Locale.ROOT));
            }
        }
    }

    @Override
    public void disable() {
        holder.unregister();
    }

    public void tryAccept(Vote vote) {
        if (isVoteAllowed(vote)) {
            getHolder().addVote(vote);
        }
    }

    public boolean isVoteAllowed(Vote vote) {
        if (!acceptOfflineVote && Bukkit.getPlayer(vote.playerName) == null) {
            return false;
        }

        if (acceptAnyServices) {
            return true;
        }

        return acceptedServices.contains(vote.serviceName.toLowerCase(Locale.ROOT));
    }
}
