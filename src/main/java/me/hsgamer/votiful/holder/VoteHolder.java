package me.hsgamer.votiful.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import me.hsgamer.topper.agent.holder.AgentDataHolder;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.spigot.agent.runnable.SpigotRunnableAgent;
import me.hsgamer.topper.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.storage.simple.converter.MapEntryConverter;
import me.hsgamer.topper.storage.simple.converter.SqlEntryConverter;
import me.hsgamer.topper.storage.simple.setting.DataStorageSetting;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.agent.VoteStatsAgent;
import me.hsgamer.votiful.agent.VoteSyncAgent;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.manager.VoteManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteHolder extends AgentDataHolder<VoteKey, VoteValue> {
    private final Votiful plugin;
    private final StorageAgent<VoteKey, VoteValue> storageAgent;
    private final VoteStatsAgent voteStatsAgent;

    public VoteHolder(Votiful plugin, String name) {
        super(name);
        this.plugin = plugin;

        storageAgent = new StorageAgent<>(plugin.getLogger(), this, plugin.get(VoteManager.class).getSupplier().getStorage(name, new DataStorageSetting<VoteKey, VoteValue>() {
            @Override
            public FlatEntryConverter<VoteKey, VoteValue> getFlatEntryConverter() {
                return new FlatEntryConverter<VoteKey, VoteValue>() {
                    @Override
                    public VoteKey toKey(String key) {
                        String[] split = key.split(";", 2);
                        if (split.length != 2) {
                            return null;
                        }
                        try {
                            return new VoteKey(split[0], UUID.fromString(split[1]));
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public String toRawKey(VoteKey key) {
                        return key.serverName + ";" + key.id.toString();
                    }

                    @Override
                    public VoteValue toValue(String value) {
                        String[] split = value.split(";", 4);
                        if (split.length != 4) {
                            return null;
                        }
                        try {
                            return new VoteValue(split[0], split[1], split[2], Long.parseLong(split[3]));
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public String toRawValue(VoteValue object) {
                        return object.serviceName + ";" + object.playerName + ";" + object.address + ";" + object.timestamp;
                    }
                };
            }

            @Override
            public MapEntryConverter<VoteKey, VoteValue> getMapEntryConverter() {
                return new MapEntryConverter<VoteKey, VoteValue>() {
                    @Override
                    public VoteKey toKey(Map<String, Object> map) {
                        Object serverName = map.get("serverName");
                        Object id = map.get("id");
                        if (serverName == null || id == null) {
                            return null;
                        }
                        try {
                            return new VoteKey(serverName.toString(), UUID.fromString(id.toString()));
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public VoteValue toValue(Map<String, Object> map) {
                        Object serviceName = map.get("serviceName");
                        Object playerName = map.get("playerName");
                        Object address = map.get("address");
                        Object timestamp = map.get("timestamp");
                        if (serviceName == null || playerName == null || address == null || timestamp == null) {
                            return null;
                        }
                        try {
                            return new VoteValue(
                                    serviceName.toString(),
                                    playerName.toString(),
                                    address.toString(),
                                    Long.parseLong(timestamp.toString())
                            );
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    public Map<String, Object> toRawKey(VoteKey key) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("serverName", key.serverName);
                        map.put("id", key.id.toString());
                        return map;
                    }

                    @Override
                    public Map<String, Object> toRawValue(VoteValue value) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("serviceName", value.serviceName);
                        map.put("playerName", value.playerName);
                        map.put("address", value.address);
                        map.put("timestamp", value.timestamp);
                        return map;
                    }
                };
            }

            @Override
            public SqlEntryConverter<VoteKey, VoteValue> getSqlEntryConverter() {
                return new SqlEntryConverter<VoteKey, VoteValue>() {
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
                };
            }
        }));
        addAgent(new SpigotRunnableAgent<>(storageAgent, AsyncScheduler.get(plugin), 10));

        VoteSyncAgent voteSyncAgent = new VoteSyncAgent(this);
        addAgent(new SpigotRunnableAgent<>(voteSyncAgent, AsyncScheduler.get(plugin), 10));

        voteStatsAgent = new VoteStatsAgent(this);
        addAgent(new SpigotRunnableAgent<>(voteStatsAgent, AsyncScheduler.get(plugin), 10));
    }

    public StorageAgent<VoteKey, VoteValue> getStorageAgent() {
        return storageAgent;
    }

    public VoteStatsAgent getVoteStatsAgent() {
        return voteStatsAgent;
    }

    public DataEntry<VoteKey, VoteValue> createEntry() {
        return getOrCreateEntry(new VoteKey(plugin.get(MainConfig.class).getServerName()));
    }
}
