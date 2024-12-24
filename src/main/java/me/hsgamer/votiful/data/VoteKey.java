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

    public static VoteKey fromString(String string) {
        String[] split = string.split(";", 2);
        return new VoteKey(split[0], UUID.fromString(split[1]));
    }

    @Override
    public String toString() {
        return serverName + ";" + id.toString();
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
}
