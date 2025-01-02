package me.hsgamer.votiful.command;

import com.vexsoftware.votifier.NuVotifierBukkit;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.net.VotifierSession;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.stream.IntStream;

public class StressVoteCommand extends Command {
    public StressVoteCommand() {
        super("stressvote", "Stress Vote", "/stressvote <name> [amount]", Collections.emptyList());
        setPermission("votiful.stress");
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
        NuVotifierBukkit nuVotifierBukkit = JavaPlugin.getPlugin(NuVotifierBukkit.class);
        IntStream.rangeClosed(1, amount)
                .parallel()
                .mapToObj(i -> new Vote("TestVote", name, "127.0.0.1", Long.toString(System.currentTimeMillis(), 10)))
                .forEach(vote -> nuVotifierBukkit.onVoteReceived(vote, VotifierSession.ProtocolVersion.TEST, "localhost.test"));
        sender.sendMessage("Sent " + amount + " vote(s) to " + name);
        return true;
    }
}
