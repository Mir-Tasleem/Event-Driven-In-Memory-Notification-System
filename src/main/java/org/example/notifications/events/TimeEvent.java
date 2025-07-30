package org.example.notifications.events;

import java.time.LocalDateTime;

public class TimeEvent extends AbstractEvent {
    public TimeEvent(String description,Priority priority, LocalDateTime timeStamp) {
        super(String.format("Task[Priority=%s]: %s", Priority.LOW, description),Priority.LOW,timeStamp);
    }
}