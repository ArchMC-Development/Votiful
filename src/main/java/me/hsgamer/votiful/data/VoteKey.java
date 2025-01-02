package me.hsgamer.votiful.data;

import java.util.Objects;
import java.util.UUID;

public class VoteKey {
    public final String serverName;
    public final UUID id;

    public VoteKey(String serverName, UUID id) {
        this.serverName = serverName;
        this.id = id;
    }

    public VoteKey(String serverName) {
        this(serverName, UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VoteKey voteKey = (VoteKey) o;
        return Objects.equals(serverName, voteKey.serverName) && Objects.equals(id, voteKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverName, id);
    }

    @Override
    public String toString() {
        return "VoteKey{" +
                "serverName='" + serverName + '\'' +
                ", id=" + id +
                '}';
    }
}
