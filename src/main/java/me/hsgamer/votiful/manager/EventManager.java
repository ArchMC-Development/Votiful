package me.hsgamer.votiful.manager;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.hsgamer.votiful.Votiful;
import me.hsgamer.votiful.config.MainConfig;
import me.hsgamer.votiful.data.VoteTableDiffSnapshot;
import me.hsgamer.votiful.event.Event;
import me.hsgamer.votiful.event.GlobalEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class EventManager implements Loadable {
    private final Votiful plugin;
    private final List<Event> events = new ArrayList<>();

    public EventManager(Votiful plugin) {
        this.plugin = plugin;
    }

    public List<Event> getEvents() {
        return events;
    }

    public List<String> handle(VoteTableDiffSnapshot snapshot) {
        List<String> commands = new ArrayList<>();
        for (Event event : events) {
            commands.addAll(event.handle(snapshot));
        }
        return commands;
    }

    @Override
    public void enable() {
        Map<String, Map<String, Object>> eventSettings = plugin.get(MainConfig.class).getEvents();
        for (Map.Entry<String, Map<String, Object>> entry : eventSettings.entrySet()) {
            String eventName = entry.getKey();
            Map<String, Object> eventSetting = entry.getValue();
            try {
                GlobalEvent globalEvent = GlobalEvent.create(eventSetting);
                events.add(globalEvent);
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error while creating event: " + eventName, e);
            }
        }
    }

    @Override
    public void disable() {
        events.clear();
    }
}
