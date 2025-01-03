package me.hsgamer.votiful.data;

import java.util.Objects;

public class VoteKey {
    public final String serverName;
    public final String playerName;
    public final String serviceName;

    public VoteKey(String serverName, String playerName, String serviceName) {
        this.serverName = serverName;
        this.playerName = playerName;
        this.serviceName = serviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteKey key = (VoteKey) o;
        return Objects.equals(serverName, key.serverName) && Objects.equals(playerName, key.playerName) && Objects.equals(serviceName, key.serviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverName, playerName, serviceName);
    }

    @Override
    public String toString() {
        return "VoteKey{" +
                "serverName='" + serverName + '\'' +
                ", playerName='" + playerName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
