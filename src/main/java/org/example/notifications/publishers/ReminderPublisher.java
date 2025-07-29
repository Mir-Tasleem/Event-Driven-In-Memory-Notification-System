package org.example.notifications.publishers;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.TimeEvent;

import java.time.LocalDateTime;

public class ReminderPublisher implements Runnable {
    private final EventBus eventBus;
    private final int intervalSeconds;
    private volatile boolean running = true;

    public ReminderPublisher(EventBus eventBus, int intervalSeconds) {
        this.eventBus = eventBus;
        this.intervalSeconds = intervalSeconds;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            TimeEvent event = new TimeEvent("Scheduled reminder at " + LocalDateTime.now());
            eventBus.publishEvent(event);
            try {
                Thread.sleep(intervalSeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}

