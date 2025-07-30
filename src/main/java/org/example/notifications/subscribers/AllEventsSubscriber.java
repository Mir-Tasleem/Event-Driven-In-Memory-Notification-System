package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class AllEventsSubscriber extends AbstractSubscriber<Event> {
    public AllEventsSubscriber(String name) {
        super(name);
    }

    @Override
    public String notify(Event e) {
        return String.format("%s[%s] recieved [%s] : %s",getName(),getId(),e.getEventType(),e.getPayload());

    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> true;
    }
}