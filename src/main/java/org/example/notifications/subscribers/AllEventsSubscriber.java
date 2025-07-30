package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class AllEventsSubscriber extends AbstractSubscriber<Event> {
    public AllEventsSubscriber(String name) {
        super(name);
    }

    @Override
    public String notify(Event e) {
        return getName() + " received: " + e.getEventType() + " - " + e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> true;
    }
}