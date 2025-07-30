package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CompositeSubscriber extends AbstractSubscriber<Event> {
    private final List<Subscriber<Event>> subscribers = new ArrayList<>();

    public CompositeSubscriber(String name) {
        super(name);
    }

    public void addSubscriber(Subscriber<Event> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public String notify(Event event) {
        StringBuilder notification = new StringBuilder();
        for (Subscriber<Event> subscriber : subscribers) {
            if (subscriber.getFilter().test(event)) {
                notification.append(subscriber.notify(event)).append("\n");
            }
        }
        return notification.toString().trim();
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> subscribers.stream().allMatch(s -> s.getFilter().test(event));
    }
}