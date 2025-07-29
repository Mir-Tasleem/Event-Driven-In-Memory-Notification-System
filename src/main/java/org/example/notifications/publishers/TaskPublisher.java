package org.example.notifications.publishers;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.NewTaskEvent;
import org.example.notifications.events.Priority;

public class TaskPublisher {
    private final EventBus eventBus;

    public TaskPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publishNewTask(String taskId, String description, Priority priority) {
        NewTaskEvent event = new NewTaskEvent(description, priority);
        eventBus.publishEvent(event);
    }
}
