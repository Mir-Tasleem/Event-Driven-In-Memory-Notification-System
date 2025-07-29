package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public interface Subscriber {
    String getName();
    String notify(Event e);
    Predicate<Event> getFilter();
}
