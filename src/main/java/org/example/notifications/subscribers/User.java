package org.example.notifications.subscribers;

import org.example.notifications.events.Event;
import org.example.notifications.events.Priority;
import org.example.notifications.util.IdGenerator;

public class User {
    private final String userId;
    private final String name;
    private CompositeSubscriber currentSubscriber;

    public User(String name) {
        this.userId = IdGenerator.generateSubscriberId();
        this.name = name;
        this.currentSubscriber = new CompositeSubscriber(userId, name);
    }

    public void addSubscription(SubscriberType type, Object... params) {
        switch (type) {
            case ALL_EVENTS:
                currentSubscriber.addSubscriber(new AllEventsSubscriber(this.userId,this.name));
                break;
            case PRIORITY:
                Priority priority = (Priority) params[0];
                currentSubscriber.addSubscriber(new PrioritySubscriber(this.userId,this.name, priority));
                break;
            case TASK_ONLY:
                currentSubscriber.addSubscriber(new TaskOnlySubscriber(this.userId,this.name));
                break;
            case TIME_WINDOW:
                int startHour = (int) params[0];
                int endHour = (int) params[1];
                currentSubscriber.addSubscriber(new TimeWindowSubscriber(this.userId,this.name, startHour, endHour));
                break;
        }
    }



    public Subscriber<Event> getSubscriber() {
        return currentSubscriber;
    }
}