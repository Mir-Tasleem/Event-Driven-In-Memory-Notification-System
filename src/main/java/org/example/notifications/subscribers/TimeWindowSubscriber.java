package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class TimeWindowSubscriber implements Subscriber{
    private final String name;
    private final int startHour;
    private final int endHour;

    public TimeWindowSubscriber(String name, int startHour, int endHour) {
        this.name = name;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public String getName(){
        return name;
    }

    @Override
    public String notify(Event e){
        return name +  " recieved (within time window) " +e.getPayload();
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> {
            int hour=event.getTimeStamp().getHour();
            return hour>=startHour && hour<=endHour;
        };
    }
}
