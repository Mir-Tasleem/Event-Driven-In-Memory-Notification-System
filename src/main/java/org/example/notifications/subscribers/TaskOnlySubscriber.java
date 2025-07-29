package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class TaskOnlySubscriber implements Subscriber{
    private final String name;
    public TaskOnlySubscriber(String name){
        this.name=name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String notify(Event e){
        return name + " received task: " + e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter(){
        return event -> event.getEventType().equals("NEW_TASK");
    }
}
