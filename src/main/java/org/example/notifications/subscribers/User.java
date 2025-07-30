package org.example.notifications.subscribers;

import org.example.notifications.events.Event;
import org.example.notifications.events.Priority;

public class User {
    private final String userId;
    private final String name;
    private Subscriber<Event> currentSubscriber;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
        this.currentSubscriber = new CompositeSubscriber(name);
    }

    public void addSubscription(SubscriberType type, Object... params) {
        switch (type) {
            case ALL_EVENTS:
                ((CompositeSubscriber) currentSubscriber).addSubscriber(new AllEventsSubscriber(name));
                break;
            case PRIORITY:
                Priority priority = (Priority) params[0];
                ((CompositeSubscriber) currentSubscriber).addSubscriber(new PrioritySubscriber(name, priority));
                break;
            case TASK_ONLY:
                ((CompositeSubscriber) currentSubscriber).addSubscriber(new TaskOnlySubscriber(name));
                break;
            case TIME_WINDOW:
                int startHour = (int) params[0];
                int endHour = (int) params[1];
                ((CompositeSubscriber) currentSubscriber).addSubscriber(new TimeWindowSubscriber(name, startHour, endHour));
                break;
        }
    }

    public Subscriber<Event> getSubscriber() {
        return currentSubscriber;
    }

    public void clearSubscriptions() {
        this.currentSubscriber = new CompositeSubscriber(name);
    }
}