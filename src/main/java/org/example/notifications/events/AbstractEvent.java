package org.example.notifications.events;

import org.example.notifications.util.IdGenerator;

import java.time.LocalDateTime;

public abstract class AbstractEvent implements Event {
    private final String eventId;
    private final LocalDateTime timeStamp;
    private  String payload;
    private final Priority priority;

    protected AbstractEvent(Priority priority, LocalDateTime timeStamp) {
        this.eventId = IdGenerator.generateEventId();
        this.timeStamp = timeStamp;
        this.priority = priority;
    }

    public void setPayload(String payload){
        this.payload=payload;
    }


    @Override
    public String getEventType() {
        return this.getClass().getSimpleName().toUpperCase();
    }

    @Override
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    public String getEventId() {
        return eventId;
    }
}