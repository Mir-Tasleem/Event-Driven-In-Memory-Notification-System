package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public interface Subscriber<T extends Event> {
    String getId();
    String getName();
    String notify(T event);
    Predicate<T> getFilter();
}
