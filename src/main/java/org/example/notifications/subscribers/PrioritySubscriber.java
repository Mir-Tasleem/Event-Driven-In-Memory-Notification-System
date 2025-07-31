package org.example.notifications.subscribers;

import org.example.notifications.events.Event;
import org.example.notifications.events.Priority;

import java.util.function.Predicate;

public class PrioritySubscriber extends AbstractSubscriber<Event> {
    private final Priority priority;

    public PrioritySubscriber(String id,String name, Priority priority) {
        super(id,name);
        this.priority = priority;
    }

    @Override
    public String notify(Event e) {
        return String.format("%s[%s] recieved [%s] priority event: %s",getName(),getId(),e.getPriority(),e.getPayload());
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> event.getPriority() != null && event.getPriority().equals(priority);
    }
}