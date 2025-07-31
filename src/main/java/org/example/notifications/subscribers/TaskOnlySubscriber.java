package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class TaskOnlySubscriber extends AbstractSubscriber<Event> {
    public TaskOnlySubscriber(String id, String name) {
        super(id,name);
    }

    @Override
    public String notify(Event e) {
        return String.format("%s[%s] recieved [%s] : %s",getName(),getId(),e.getEventType(),e.getPayload());
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> event.getEventType().equals("NEWTASKEVENT");
    }
}