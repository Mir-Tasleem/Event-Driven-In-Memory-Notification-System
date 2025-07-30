package org.example.notifications.publishers;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.NewTaskEvent;
import org.example.notifications.events.Priority;
import org.example.notifications.util.IdGenerator;

import java.time.LocalDateTime;

public class TaskPublisher {
    private final String publisherId;
    private final EventBus<NewTaskEvent> eventBus;

    public TaskPublisher(EventBus<? super NewTaskEvent> eventBus) {
        this.eventBus = (EventBus<NewTaskEvent>) eventBus;
        this.publisherId= IdGenerator.generatePublisherId();
    }

    public void publishNewTask(String description, Priority priority, LocalDateTime timeStamp) {
        NewTaskEvent event = new NewTaskEvent(description, priority,timeStamp);
        eventBus.publishEvent(event);
    }

    public String getPublisherId() {
        return publisherId;
    }
}
