package me.hsgamer.votiful.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.hook.placeholderapi.VoteExpansion;

public class PlaceholderAPIHook implements Loadable {
    private final Votiful plugin;
    private Runnable onDisable;

    public PlaceholderAPIHook(Votiful plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            VoteExpansion expansion = new VoteExpansion(plugin);
            expansion.register();
            onDisable = expansion::unregister;
        }
    }

    @Override
    public void disable() {
        if (onDisable != null) {
            onDisable.run();
        }
    }
}
