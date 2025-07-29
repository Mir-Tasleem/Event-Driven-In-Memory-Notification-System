package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class PrioritySubscriber implements Subscriber{
    private final String name;
    private final String priority;

    public PrioritySubscriber(String name,String priority){
        this.name=name;
        this.priority=priority;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String notify(Event e){
        return name + " received " +e.getPriority()+ "-priority event"+ e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter(){
        return event -> event.getPriority().toString().equals(priority.toUpperCase());
    }
}
