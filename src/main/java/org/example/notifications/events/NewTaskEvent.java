package org.example.notifications.events;

import java.time.LocalDateTime;

public class NewTaskEvent extends AbstractEvent {
    private final String description;

    public NewTaskEvent(String description, Priority priority, LocalDateTime timeStamp) {
        super(
                String.format("Task[Priority=%s]: %s", priority, description),
                priority,
                timeStamp
        );
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}