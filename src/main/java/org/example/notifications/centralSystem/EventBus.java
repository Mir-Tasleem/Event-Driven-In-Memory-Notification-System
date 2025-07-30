package org.example.notifications.centralSystem;

import org.example.notifications.events.Event;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.subscribers.CompositeSubscriber;
import org.example.notifications.subscribers.Subscriber;
import org.example.notifications.subscribers.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EventBus<T extends Event> {
    Logger logger= LoggerFactory.getLogger(EventBus.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final EventLogger eventLogger;
    private final List<Subscriber<T>> subscribers = new CopyOnWriteArrayList<>();
    private final List<T> eventHistory = new CopyOnWriteArrayList<>();
    private final Map<T, List<String>> notificationLog = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final AtomicInteger eventCount = new AtomicInteger(0);

    public EventBus(EventLogger eventLogger, int threadPoolSize) {
        this.eventLogger = eventLogger;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void registerUserSubscriptions(User user) {
        registerSubscriber((Subscriber<T>) user.getSubscriber());
    }

    public void registerMultipleUsers(List<User> users) {
        users.forEach(this::registerUserSubscriptions);
    }

    public void registerSubscribers(List<Subscriber<T>> subscribers) {
        this.subscribers.addAll(subscribers);
    }


    public void registerSubscriber(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    public void publishEvent(T event) {
        eventLogger.logEvent(event);
        eventHistory.add(event);
        List<String> notified = new CopyOnWriteArrayList<>();

        for (Subscriber<T> subscriber : subscribers) {
            if (subscriber.getFilter().test(event)) {
                executorService.submit(() -> {
                    try {
                        String result = subscriber.notify(event);
                        // Add subscriber type info to logs
                        String logMsg = subscriber instanceof CompositeSubscriber ?
                                "[Composite Notify] " + result :
                                "[Notify] " + result;
                        logger.info(logMsg);
                    } catch (Exception ex) {
                        logger.error("Failed to notify {} : {}", subscriber.getName(), ex.getMessage());
                    }
                });
                notified.add(subscriber.getClass().getSimpleName() + ": " + subscriber.getName());
            }
        }

        notificationLog.put(event, notified);
        eventCount.incrementAndGet();

        if (event.getClass().getSimpleName().equals("NewTaskEvent")) {
            scheduleReminder(event);
        }
    }

    private void scheduleReminder(T event) {
        long delayMillis = Duration.between(LocalDateTime.now(), event.getTimeStamp().minusMinutes(2)).toMillis();

        if (delayMillis <= 0) {
            logger.warn("[Reminder] Reminder time already passed for event: {}", event.getEventId());
            return;
        }

        scheduler.schedule(() -> {
            logger.info("[Reminder] Notifying subscribers 5 minutes before: {}", event.getEventId());
            for (Subscriber<T> subscriber : subscribers) {
                if (subscriber.getFilter().test(event)) {
                    executorService.submit(() -> {
                        try {
                            String result = subscriber.notify(event);
                            logger.info("[Reminder Notify] : {}", result);
                        } catch (Exception ex) {
                            logger.error("[Reminder Error] Failed to notify {} : {}", subscriber.getName(), ex.getMessage());
                        }
                    });
                }
            }
        }, delayMillis, TimeUnit.MILLISECONDS);
    }


    public List<T> getEventHistory() {
        return eventHistory;
    }

    public Map<T, List<String>> getNotificationLog() {
        return notificationLog;
    }

    public int getTotalPublishedEvents(){
        return eventHistory.size();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public void shutdownNow() {
        executorService.shutdownNow();
    }
}
