package me.hsgamer.votiful.data;

import me.hsgamer.topper.storage.flat.converter.ComplexFlatValueConverter;
import me.hsgamer.topper.storage.flat.converter.StringFlatValueConverter;
import me.hsgamer.topper.storage.flat.core.FlatValueConverter;
import me.hsgamer.topper.storage.sql.converter.ComplexSqlValueConverter;
import me.hsgamer.topper.storage.sql.converter.StringSqlValueConverter;
import me.hsgamer.topper.storage.sql.core.SqlValueConverter;

import java.util.Objects;

public class VoteKey {
    public static final FlatValueConverter<VoteKey> FLAT_CONVERTER = ComplexFlatValueConverter.<VoteKey>builder()
            .constructor(() -> new VoteKey("", "", ""))
            .entry(new StringFlatValueConverter(), voteKey -> voteKey.serverName, (voteKey, value) -> new VoteKey(value, voteKey.playerName, voteKey.serviceName))
            .entry(new StringFlatValueConverter(), voteKey -> voteKey.playerName, (voteKey, value) -> new VoteKey(voteKey.serverName, value, voteKey.serviceName))
            .entry(new StringFlatValueConverter(), voteKey -> voteKey.serviceName, (voteKey, value) -> new VoteKey(voteKey.serverName, voteKey.playerName, value))
            .stringSeparator(";")
            .build();
    public static final SqlValueConverter<VoteKey> SQL_CONVERTER = ComplexSqlValueConverter.<VoteKey>builder()
            .constructor(() -> new VoteKey("", "", ""))
            .entry(new StringSqlValueConverter("serverName", false, 256), voteKey -> voteKey.serverName, (voteKey, value) -> new VoteKey(value, voteKey.playerName, voteKey.serviceName))
            .entry(new StringSqlValueConverter("playerName", false, 256), voteKey -> voteKey.playerName, (voteKey, value) -> new VoteKey(voteKey.serverName, value, voteKey.serviceName))
            .entry(new StringSqlValueConverter("serviceName", false, 256), voteKey -> voteKey.serviceName, (voteKey, value) -> new VoteKey(voteKey.serverName, voteKey.playerName, value))
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
