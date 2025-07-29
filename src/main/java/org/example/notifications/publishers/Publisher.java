package org.example.notifications.publishers;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.Event;

public class Publisher {

    private final EventBus eventBus;

    public Publisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publish(Event event) {
        System.out.println("[Publisher] Publishing event: " + event.getEventType());
        eventBus.publishEvent(event);
    }
}
