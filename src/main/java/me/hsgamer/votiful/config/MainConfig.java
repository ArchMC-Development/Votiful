package me.hsgamer.votiful.config;

import me.hsgamer.hscore.config.annotation.Comment;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.votiful.config.converter.StringListConverter;
import me.hsgamer.votiful.config.converter.StringStringObjectMapConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface MainConfig {
    @ConfigPath("group")
    @Comment("The group including the server")
    default String getGroup() {
        return "default";
    }

    @ConfigPath("server-name")
    @Comment("The server name")
    default String getServerName() {
        return "server";
    }

    @ConfigPath("storage-type")
    @Comment({
            "The storage type",
            "Available types: FLAT, MYSQL, SQLITE, NEW-SQLITE, JSON, YAML"
    })
    default String getStorageType() {
        return "FLAT";
    }

    @ConfigPath(value = "events", converter = StringStringObjectMapConverter.class)
    @Comment("The event settings")
    default Map<String, Map<String, Object>> getEvents() {
        return Collections.emptyMap();
    }

    @ConfigPath({"tasks", "save", "interval"})
    @Comment("The interval in ticks to do the saving task")
    default long getTasksSaveInterval() {
        return 10L;
    }

    @ConfigPath({"tasks", "save", "entry-per-call"})
    @Comment("The amount of entries to save per call")
    default int getTasksSaveEntryPerCall() {
        return 10;
    }

    @ConfigPath({"tasks", "sync", "enable"})
    @Comment({
            "Enable the sync task",
            "This will sync the votes from other servers within the group"
    })
    default boolean isTasksSyncEnable() {
        return false;
    }

    @ConfigPath({"tasks", "sync", "interval"})
    @Comment("The interval in ticks to do the sync task")
    default long getTasksSyncInterval() {
        return 10L;
    }

    @ConfigPath({"tasks", "stats", "interval"})
    @Comment("The interval in ticks to do the stats task")
    default long getTasksStatsInterval() {
        return 10L;
    }

    @ConfigPath({"tasks", "event", "interval"})
    @Comment("The interval in ticks to do the event task")
    default long getTasksEventInterval() {
        return 10L;
    }

    @ConfigPath({"vote", "offline"})
    @Comment("Should the plugin handle offline votes?")
    default boolean isVotesOffline() {
        return false;
    }

    @ConfigPath(value = {"vote", "services"}, converter = StringListConverter.class)
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
