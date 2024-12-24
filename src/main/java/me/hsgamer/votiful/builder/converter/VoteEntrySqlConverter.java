package me.hsgamer.votiful.builder.converter;

import me.hsgamer.topper.agent.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class VoteEntrySqlConverter implements SqlEntryConverter<VoteKey, VoteValue> {
    public static final VoteEntrySqlConverter INSTANCE = new VoteEntrySqlConverter();

    private VoteEntrySqlConverter() {
    }

    @Override
    public String[] getKeyColumns() {
        return new String[]{
                "server_name",
                "id"
        };
    }

    @Override
    public String[] getValueColumns() {
        return new String[]{
                "service_name",
                "player_name",
                "address",
                "timestamp"
        };
    }

    @Override
    public String[] getKeyColumnDefinitions() {
        return new String[]{
                "`server_name` VARCHAR(36) NOT NULL",
                "`id` VARCHAR(36) NOT NULL"
        };
    }

    @Override
    public String[] getValueColumnDefinitions() {
        return new String[]{
                "`service_name` VARCHAR(255) NOT NULL",
                "`player_name` VARCHAR(255) NOT NULL",
                "`address` VARCHAR(255) NOT NULL",
                "`timestamp` BIGINT NOT NULL"
        };
    }

    @Override
    public Object[] toKeyQueryValues(VoteKey key) {
        return new Object[]{
                key.serverName,
                key.id.toString()
        };
    }

    @Override
    public Object[] toValueQueryValues(VoteValue value) {
        return new Object[]{
                value.serviceName,
                value.playerName,
                value.address,
                value.timestamp
        };
    }

    @Override
    public VoteKey getKey(ResultSet resultSet) throws SQLException {
        return new VoteKey(
                resultSet.getString("server_name"),
                UUID.fromString(resultSet.getString("id"))
        );
    }

    @Override
    public VoteValue getValue(ResultSet resultSet) throws SQLException {
        return new VoteValue(
                resultSet.getString("service_name"),
                resultSet.getString("player_name"),
                resultSet.getString("address"),
                resultSet.getLong("timestamp")
        );
    }
}
