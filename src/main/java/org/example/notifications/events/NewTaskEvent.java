package org.example.notifications.events;

import java.time.LocalDateTime;

public class NewTaskEvent extends AbstractEvent {
    private final String description;

    public NewTaskEvent(String description, Priority priority, LocalDateTime timeStamp) {
        super(priority, timeStamp);  // eventId is now generated
        this.description = description;
        setPayload(String.format("Task[Event Id=%s, Priority=%s]: %s", getEventId(), priority, description));
    }


    public String getDescription() {
        return description;
    }
}