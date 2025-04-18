package me.hsgamer.votiful.config;

import me.hsgamer.hscore.config.annotation.Comment;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.votiful.config.converter.StringListConverter;
import me.hsgamer.votiful.config.converter.StringStringObjectMapConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface MainConfig {
    @ConfigPath(value = "group")
    @Comment("The group including the server")
    default String getGroup() {
        return "default";
    }

    @ConfigPath(value = "server-name", priority = 1)
    @Comment("The server name")
    default String getServerName() {
        return "server";
    }

    @ConfigPath(value = "storage-type", priority = 2)
    @Comment({
            "The storage type",
            "Available types: FLAT, MYSQL, SQLITE, NEW-SQLITE, JSON, YAML"
    })
    default String getStorageType() {
        return "FLAT";
    }

    @ConfigPath(value = "events", converter = StringStringObjectMapConverter.class, priority = 3)
    @Comment("The event settings")
    default Map<String, Map<String, Object>> getEvents() {
        return Collections.emptyMap();
    }

    @ConfigPath(value = {"tasks", "save", "interval"}, priority = 4)
    @Comment("The interval in ticks to do the saving task")
    default long getTasksSaveInterval() {
        return 10L;
    }

    @ConfigPath(value = {"tasks", "save", "entry-per-call"}, priority = 4)
    @Comment("The amount of entries to save per call")
    default int getTasksSaveEntryPerCall() {
        return 10;
    }

    @ConfigPath(value = {"tasks", "sync", "enable"}, priority = 4)
    @Comment({
            "Enable the sync task",
            "This will sync the votes from other servers within the group"
    })
    default boolean isTasksSyncEnable() {
        return false;
    }

    @ConfigPath(value = {"tasks", "sync", "interval"}, priority = 4)
    @Comment("The interval in ticks to do the sync task")
    default long getTasksSyncInterval() {
        return 10L;
    }

    @ConfigPath(value = {"tasks", "stats", "interval"}, priority = 4)
    @Comment("The interval in ticks to do the stats task")
    default long getTasksStatsInterval() {
        return 10L;
    }

    @ConfigPath(value = {"tasks", "event", "interval"}, priority = 4)
    @Comment("The interval in ticks to do the event task")
    default long getTasksEventInterval() {
        return 10L;
    }

    @ConfigPath(value = {"vote", "offline"}, priority = 5)
    @Comment("Should the plugin handle offline votes?")
    default boolean isVotesOffline() {
        return false;
    }

    @ConfigPath(value = {"vote", "services"}, converter = StringListConverter.class, priority = 5)
    @Comment({
            "The accepted vote services",
            "Use '*' to accept all services"
    })
    default List<String> getVoteServices() {
        return Collections.singletonList("*");
    }

    default boolean isVoteServiceEnabled(String service) {
        List<String> services = getVoteServices();
        return services.contains("*") || services.contains(service);
    }
}
