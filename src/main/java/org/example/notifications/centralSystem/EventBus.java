package org.example.notifications.centralSystem;

import org.example.notifications.Main;
import org.example.notifications.events.Event;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.subscribers.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EventBus {
    final Logger logger = LoggerFactory.getLogger(Main.class);
    private final EventLogger eventLogger;

    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private final List<Event> eventHistory = new CopyOnWriteArrayList<>();
    private final Map<Event, List<String>> notificationLog = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final AtomicInteger eventCount = new AtomicInteger(0);

    public EventBus(EventLogger eventLogger,int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.eventLogger=eventLogger;
    }

    public void registerSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void publishEvent(Event event) {
        eventLogger.logEvent(event);
        eventHistory.add(event);
        List<String> notified = new CopyOnWriteArrayList<>();

        for (Subscriber subscriber : subscribers) {
            if (subscriber.getFilter().test(event)) {
                executorService.submit(() -> {
                    String result = subscriber.notify(event);
                    logger.info("[Notify] {}", result);
                });
                notified.add(subscriber.getName());
            }
        }

        notificationLog.put(event, notified);
        eventCount.incrementAndGet();

    }

    public List<Event> getEventHistory() {
        return eventHistory;
    }

    public Map<Event, List<String>> getNotificationLog() {
        return notificationLog;
    }

    public int getTotalPublishedEvents() {
        return eventCount.get();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }
}
