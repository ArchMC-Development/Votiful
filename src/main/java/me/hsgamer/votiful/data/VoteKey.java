package me.hsgamer.votiful.data;

import me.hsgamer.topper.storage.simple.converter.ComplexValueConverter;
import me.hsgamer.topper.storage.simple.converter.StringConverter;
import me.hsgamer.topper.storage.simple.converter.ValueConverter;

import java.util.Objects;

public class VoteKey {
    public static final ValueConverter<VoteKey> CONVERTER = ComplexValueConverter.<VoteKey>builder()
            .constructor(() -> new VoteKey("", "", ""))
            .entry(new StringConverter("serverName", false, 256), voteKey -> voteKey.serverName, (voteKey, value) -> new VoteKey(value, voteKey.playerName, voteKey.serviceName))
            .entry(new StringConverter("playerName", false, 256), voteKey -> voteKey.playerName, (voteKey, value) -> new VoteKey(voteKey.serverName, value, voteKey.serviceName))
            .entry(new StringConverter("serviceName", false, 256), voteKey -> voteKey.serviceName, (voteKey, value) -> new VoteKey(voteKey.serverName, voteKey.playerName, value))
            .stringSeparator(";")
            .build();

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
