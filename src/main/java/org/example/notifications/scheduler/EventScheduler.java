package org.example.notifications.scheduler;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.TimeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventScheduler implements Runnable {
    private final EventBus eventBus;
    private final int intervalSeconds;
    private final ScheduledExecutorService executorService;

    public EventScheduler(EventBus eventBus, int intervalSeconds) {
        this.eventBus = eventBus;
        this.intervalSeconds = intervalSeconds;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        executorService.scheduleAtFixedRate(() -> {
            TimeEvent event = new TimeEvent("Recurring Time Event");
            eventBus.publishEvent(event);
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    public void stop() {
        executorService.shutdown();
    }
}
