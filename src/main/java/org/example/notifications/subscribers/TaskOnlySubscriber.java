package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class TaskOnlySubscriber extends AbstractSubscriber<Event> {
    public TaskOnlySubscriber(String name) {
        super(name);
    }

    @Override
    public String notify(Event e) {
        return getName() + " received task: " + e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> event.getEventType().equals("NEWTASKEVENT");
    }
}