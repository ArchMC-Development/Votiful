package me.hsgamer.votiful.command;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.listener.VoteListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.stream.IntStream;

public class StressVoteCommand extends Command {
    private final Votiful plugin;

    public StressVoteCommand(Votiful plugin) {
        super("stressvote", "Stress Vote", "/stressvote <name> [amount]", Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(getUsage());
            return false;
        }
        String name = args[0];
        int amount = 1;
        if (args.length > 1) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid amount");
                return false;
            }
        }
        VoteListener listener = plugin.get(VoteListener.class);
        IntStream.rangeClosed(1, amount)
                .parallel()
                .mapToObj(i -> new Vote("TestVote", name, "127.0.0.1", Long.toString(System.currentTimeMillis(), 10)))
                .forEach(vote -> listener.onVote(new VotifierEvent(vote)));
        sender.sendMessage("Sent " + amount + " vote(s) to " + name);
        return true;
    }
}
