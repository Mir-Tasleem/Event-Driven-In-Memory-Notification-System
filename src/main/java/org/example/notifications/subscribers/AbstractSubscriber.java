package org.example.notifications.subscribers;

import org.example.notifications.events.Event;
import org.example.notifications.util.IdGenerator;


public abstract class AbstractSubscriber<T extends Event> implements Subscriber<T> {
    private final String id;
    private final String name;

    protected AbstractSubscriber(String name) {
        this.id = IdGenerator.generateSubscriberId();
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}