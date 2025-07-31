package org.example.notifications.scheduler;

import org.example.notifications.centralsystem.EventBus;
import org.example.notifications.events.Priority;
import org.example.notifications.events.TimeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class EventScheduler implements Runnable {
    Logger logger= LoggerFactory.getLogger(EventScheduler.class);

    private final EventBus<TimeEvent> eventBus;
    private final int intervalSeconds;
    private final ScheduledExecutorService executorService;

    public EventScheduler(EventBus<? super TimeEvent> eventBus, int intervalSeconds) {
        this.eventBus = (EventBus<TimeEvent>) eventBus;
        this.intervalSeconds = intervalSeconds;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
            TimeEvent event = new TimeEvent("Recurring Time Event", LocalDateTime.now());
            eventBus.publishEvent(event);
    }

    public void stop() {
        executorService.shutdown();
    }
}
