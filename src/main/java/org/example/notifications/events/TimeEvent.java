package org.example.notifications.events;

import java.time.LocalDateTime;

public class TimeEvent extends AbstractEvent {
    public TimeEvent(String description, Priority priority, LocalDateTime timeStamp) {
        super(Priority.LOW,timeStamp);
        setPayload(String.format("[Event Id=%s, Priority=%s]: %s",getEventId(), Priority.LOW, description));
    }
}