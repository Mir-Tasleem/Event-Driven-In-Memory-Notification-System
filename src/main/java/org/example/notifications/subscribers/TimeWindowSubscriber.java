package org.example.notifications.subscribers;

import org.example.notifications.events.Event;

import java.util.function.Predicate;

public class TimeWindowSubscriber extends AbstractSubscriber<Event> {
    private final int startHour;
    private final int endHour;

    public TimeWindowSubscriber(String name, int startHour, int endHour) {
        super(name);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public String notify(Event e) {
        return String.format("%s[%s] recieved [%s] : %s",getName(),getId(),e.getEventType(),e.getPayload());
    }

    @Override
    public Predicate<Event> getFilter() {
        return event -> {
            int hour = event.getTimeStamp().getHour();
            return hour >= startHour && hour <= endHour;
        };
    }
}