package org.example.notifications.subscribers;

import org.example.notifications.events.Event;
import org.example.notifications.events.Priority;

import java.util.function.Predicate;

public class PrioritySubscriber extends AbstractSubscriber<Event> {
    private final Priority priority;

    public PrioritySubscriber(String name, Priority priority) {
        super(name);
        this.priority = priority;
    }

    @Override
    public String notify(Event e) {
        return getName() + " received " + e.getPriority() + "-priority event: " + e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> event.getPriority() != null && event.getPriority().equals(priority);
    }
}