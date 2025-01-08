package me.hsgamer.votiful.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.votiful.config.converter.StringStringObjectMapConverter;

import java.util.Collections;
import java.util.Map;

public interface MainConfig {
    @ConfigPath("group")
    default String getGroup() {
        return "default";
    }

    @ConfigPath("server-name")
    default String getServerName() {
        return "server";
    }

    @ConfigPath("storage-type")
    default String getStorageType() {
        return "FLAT";
    }

    @ConfigPath(value = "events", converter = StringStringObjectMapConverter.class)
    default Map<String, Map<String, Object>> getEvents() {
        return Collections.emptyMap();
    }

    @ConfigPath({"tasks", "save", "interval"})
    default long getTasksSaveInterval() {
        return 10L;
    }

    @ConfigPath({"tasks", "save", "entry-per-call"})
    default int getTasksSaveEntryPerCall() {
        return 10;
    }

    @ConfigPath({"tasks", "sync", "enable"})
    default boolean isTasksSyncEnable() {
        return false;
    }

    @ConfigPath({"tasks", "sync", "interval"})
    default long getTasksSyncInterval() {
        return 10L;
    }

    @ConfigPath({"tasks", "stats", "interval"})
    default long getTasksStatsInterval() {
        return 10L;
    }

    @ConfigPath({"tasks", "event", "interval"})
    default long getTasksEventInterval() {
        return 10L;
    }
}
