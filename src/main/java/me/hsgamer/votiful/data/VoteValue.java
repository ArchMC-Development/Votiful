package me.hsgamer.votiful.data;

import com.vexsoftware.votifier.model.Vote;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class VoteValue {
    public final String serviceName;
    public final String playerName;
    public final String address;
    public final long timestamp;

    public VoteValue(String serviceName, String playerName, String address, long timestamp) {
        this.serviceName = serviceName;
        this.playerName = playerName;
        this.address = address;
        this.timestamp = timestamp;
    }

    public static VoteValue fromVotifier(Vote vote) {
        String serviceName = vote.getServiceName();
        String playerName = vote.getUsername();
        String address = vote.getAddress();
        long timestamp;
        try {
            timestamp = Long.parseLong(vote.getTimeStamp());
        } catch (Exception e) {
            timestamp = System.currentTimeMillis();
        }
        return new VoteValue(serviceName, playerName, address, timestamp);
    }

    public OfflinePlayer player() {
        //noinspection deprecation
        return Bukkit.getOfflinePlayer(playerName);
    }

    public UUID uuid() {
        return player().getUniqueId();
    }

    public Date date() {
        return new Date(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteValue voteValue = (VoteValue) o;
        return timestamp == voteValue.timestamp && Objects.equals(serviceName, voteValue.serviceName) && Objects.equals(playerName, voteValue.playerName) && Objects.equals(address, voteValue.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, playerName, address, timestamp);
    }
}
