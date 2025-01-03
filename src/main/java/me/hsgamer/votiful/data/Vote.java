package me.hsgamer.votiful.data;

import java.util.Objects;

public class Vote {
    public final String serviceName;
    public final String playerName;
    public final String address;
    public final long timestamp;

    public Vote(String serviceName, String playerName, String address, long timestamp) {
        this.serviceName = serviceName;
        this.playerName = playerName;
        this.address = address;
        this.timestamp = timestamp;
    }

    public static Vote fromVotifier(com.vexsoftware.votifier.model.Vote vote) {
        String serviceName = vote.getServiceName();
        String playerName = vote.getUsername();
        String address = vote.getAddress();
        long timestamp;
        try {
            timestamp = Long.parseLong(vote.getTimeStamp());
        } catch (Exception e) {
            timestamp = System.currentTimeMillis();
        }
        return new Vote(serviceName, playerName, address, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return timestamp == vote.timestamp && Objects.equals(serviceName, vote.serviceName) && Objects.equals(playerName, vote.playerName) && Objects.equals(address, vote.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, playerName, address, timestamp);
    }
}
