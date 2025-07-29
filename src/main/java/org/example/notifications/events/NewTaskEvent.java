package org.example.notifications.events;

import org.example.notifications.util.IdGenerator;

import java.time.LocalDateTime;

public class NewTaskEvent implements Event{
    private final String newTaskId;
    private final LocalDateTime timeStamp;
    private final String payload;
    private final Priority priority;

    public NewTaskEvent(String description,Priority priority){
        this.newTaskId= IdGenerator.generateEventId();
        timeStamp=LocalDateTime.now();
        this.priority=priority;
        this.payload = String.format("Task[ID=%s, Priority=%s]: %s", newTaskId, priority, description);
    }

    public String getNewTaskId(){
        return newTaskId;
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
    public Priority getPriority(){
        return priority;
    }
}
