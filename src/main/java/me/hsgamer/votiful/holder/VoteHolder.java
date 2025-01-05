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
import me.hsgamer.votiful.agent.VoteEventAgent;
import me.hsgamer.votiful.agent.VoteStatsAgent;
import me.hsgamer.votiful.agent.VoteSyncAgent;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.Vote;
import me.hsgamer.votiful.data.VoteKey;
import me.hsgamer.votiful.data.VoteValue;
import me.hsgamer.votiful.manager.StorageManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VoteHolder extends AgentDataHolder<VoteKey, VoteValue> {
    private final Votiful plugin;
    private final StorageAgent<VoteKey, VoteValue> storageAgent;
    private final VoteStatsAgent voteStatsAgent;
    private final VoteEventAgent voteEventAgent;

    public VoteHolder(Votiful plugin, String name) {
        super(name);
        this.plugin = plugin;

        storageAgent = new StorageAgent<VoteKey, VoteValue>(plugin.getLogger(), this, plugin.get(StorageManager.class).buildStorage(name, new VoteDataStorageSetting())) {
            @Override
            public void onUpdate(DataEntry<VoteKey, VoteValue> entry, VoteValue oldValue) {
                VoteKey key = entry.getKey();
                if (Objects.equals(key.serverName, plugin.get(MainConfig.class).getServerName())) {
                    super.onUpdate(entry, oldValue);
                }
            }
        };
        addAgent(storageAgent);
        addEntryAgent(storageAgent);
        addAgent(new SpigotRunnableAgent(storageAgent, AsyncScheduler.get(plugin), 10));

        VoteSyncAgent voteSyncAgent = new VoteSyncAgent(plugin, this);
        addAgent(new SpigotRunnableAgent(voteSyncAgent, AsyncScheduler.get(plugin), 10));

        voteStatsAgent = new VoteStatsAgent(this);
        addAgent(voteStatsAgent);
        addEntryAgent(voteStatsAgent);
        addAgent(new SpigotRunnableAgent(voteStatsAgent, AsyncScheduler.get(plugin), 10));

        voteEventAgent = new VoteEventAgent();
        addAgent(new SpigotRunnableAgent(voteEventAgent, AsyncScheduler.get(plugin), 10));
    }

    @Override
    protected VoteValue getDefaultValue() {
        return VoteValue.EMPTY;
    }

    public StorageAgent<VoteKey, VoteValue> getStorageAgent() {
        return storageAgent;
    }

    public VoteStatsAgent getVoteStatsAgent() {
        return voteStatsAgent;
    }

    public VoteEventAgent getVoteEventAgent() {
        return voteEventAgent;
    }

    public void addVote(Vote vote) {
        VoteKey key = new VoteKey(plugin.get(MainConfig.class).getServerName(), vote.playerName, vote.serviceName);
        getOrCreateEntry(key).setValue(entry -> entry.addVote(vote.timestamp));
    }

    private static class VoteDataStorageSetting implements DataStorageSetting<VoteKey, VoteValue> {
        @Override
        public FlatEntryConverter<VoteKey, VoteValue> getFlatEntryConverter() {
            return new FlatEntryConverter<VoteKey, VoteValue>() {
                @Override
                public VoteKey toKey(String key) {
                    String[] split = key.split(";", 3);
                    if (split.length != 3) {
                        return null;
                    }
                    return new VoteKey(split[0], split[1], split[2]);
                }

                @Override
                public String toRawKey(VoteKey key) {
                    return String.join(";", key.serverName, key.playerName, key.serviceName);
                }

                @Override
                public VoteValue toValue(String value) {
                    String[] split = value.split(";", 2);
                    if (split.length != 2) {
                        return null;
                    }
                    try {
                        return new VoteValue(Integer.parseInt(split[0]), Long.parseLong(split[1]));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                @Override
                public String toRawValue(VoteValue object) {
                    return object.vote + ";" + object.lastVoteTimestamp;
                }
            };
        }

        @Override
        public MapEntryConverter<VoteKey, VoteValue> getMapEntryConverter() {
            return new MapEntryConverter<VoteKey, VoteValue>() {
                @Override
                public VoteKey toKey(Map<String, Object> map) {
                    Object serverName = map.get("serverName");
                    Object playerName = map.get("playerName");
                    Object serviceName = map.get("serviceName");
                    if (serverName == null || playerName == null || serviceName == null) {
                        return null;
                    }
                    return new VoteKey(serverName.toString(), playerName.toString(), serviceName.toString());
                }

                @Override
                public VoteValue toValue(Map<String, Object> map) {
                    Object vote = map.get("vote");
                    Object lastVoteTimestamp = map.get("lastVoteTimestamp");
                    if (vote == null || lastVoteTimestamp == null) {
                        return null;
                    }
                    try {
                        return new VoteValue(Integer.parseInt(vote.toString()), Long.parseLong(lastVoteTimestamp.toString()));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                @Override
                public Map<String, Object> toRawKey(VoteKey key) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("serverName", key.serverName);
                    map.put("playerName", key.playerName);
                    map.put("serviceName", key.serviceName);
                    return map;
                }

                @Override
                public Map<String, Object> toRawValue(VoteValue value) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("vote", value.vote);
                    map.put("lastVoteTimestamp", value.lastVoteTimestamp);
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
                            "player_name",
                            "service_name"
                    };
                }

                @Override
                public String[] getValueColumns() {
                    return new String[]{
                            "vote",
                            "last_vote_timestamp"
                    };
                }

                @Override
                public String[] getKeyColumnDefinitions() {
                    return new String[]{
                            "`server_name` VARCHAR(255) NOT NULL",
                            "`player_name` VARCHAR(255) NOT NULL",
                            "`service_name` VARCHAR(255) NOT NULL"
                    };
                }

                @Override
                public String[] getValueColumnDefinitions() {
                    return new String[]{
                            "`vote` INT NOT NULL",
                            "`last_vote_timestamp` BIGINT NOT NULL"
                    };
                }

                @Override
                public Object[] toKeyQueryValues(VoteKey key) {
                    return new Object[]{
                            key.serverName,
                            key.playerName,
                            key.serviceName
                    };
                }

                @Override
                public Object[] toValueQueryValues(VoteValue value) {
                    return new Object[]{
                            value.vote,
                            value.lastVoteTimestamp
                    };
                }

                @Override
                public VoteKey getKey(ResultSet resultSet) throws SQLException {
                    return new VoteKey(
                            resultSet.getString("server_name"),
                            resultSet.getString("player_name"),
                            resultSet.getString("service_name")
                    );
                }

                @Override
                public VoteValue getValue(ResultSet resultSet) throws SQLException {
                    return new VoteValue(
                            resultSet.getInt("vote"),
                            resultSet.getLong("last_vote_timestamp")
                    );
                }
            };
        }
    }
}
