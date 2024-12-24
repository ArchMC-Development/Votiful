package me.hsgamer.votiful.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

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
}
