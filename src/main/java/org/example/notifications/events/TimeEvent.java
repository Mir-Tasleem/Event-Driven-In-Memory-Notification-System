package org.example.notifications.events;


import java.time.LocalDateTime;

public class TimeEvent implements Event{
    private final LocalDateTime timeStamp;
    private final String payload;

    public TimeEvent(String payload){
        this.payload = payload;
        timeStamp= LocalDateTime.now();
    }

    @Override
    public String getEventType(){
        return this.getClass().getSimpleName().toUpperCase();
    }

    @Override
    public LocalDateTime getTimeStamp(){
        return timeStamp;
    }

    @Override
    public String getPayload(){
        return payload;
    }

    @Override
    public Priority getPriority() {
        return null;
    }
}
