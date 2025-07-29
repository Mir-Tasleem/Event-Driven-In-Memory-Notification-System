package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class AllEventsSubscriber implements Subscriber{
    private final String name;
    public AllEventsSubscriber(String name){
        this.name=name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String notify(Event e){
        return name + " received: " + e.getEventType() + " - " + e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter(){
        return event -> true;
    }
}
