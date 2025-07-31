package org.example.notifications.centralsystem;

import org.example.notifications.events.Event;
import org.example.notifications.events.NewTaskEvent;
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

        if (event instanceof NewTaskEvent) {
            scheduleReminder(event);
        }
    }

    private void scheduleReminder(T event) {
        long delayMillis = Duration.between(LocalDateTime.now(), event.getTimeStamp().minusMinutes(5)).toMillis();

        if (delayMillis <= 0) {
            logger.warn("[Reminder] Reminder time already passed for event: {}", event.getEventId());
            return;
        }

        scheduler.schedule(() -> {
            logger.info("[Reminder] Starting reminder notifications for event: {}", event.getEventId());

            // Create thread-safe lists for tracking
            List<String> successfulNotifications = new CopyOnWriteArrayList<>();
            List<String> failedNotifications = new CopyOnWriteArrayList<>();
            CountDownLatch completionLatch = new CountDownLatch(subscribers.size());

            for (Subscriber<T> subscriber : subscribers) {
                if (subscriber.getFilter().test(event)) {
                    executorService.submit(() -> {
                        try {
                            String result = subscriber.notify(event);
                            successfulNotifications.add(subscriber.getName());
                            logger.debug("[Reminder Success] {} received reminder: {}",
                                    subscriber.getName(), result);
                        } catch (Exception ex) {
                            failedNotifications.add(subscriber.getName());
                            logger.error("[Reminder Error] {} failed: {}",
                                    subscriber.getName(), ex.getMessage());
                        } finally {
                            completionLatch.countDown();
                        }
                    });
                } else {
                    completionLatch.countDown();
                }
            }

            try {
                if (!completionLatch.await(30, TimeUnit.SECONDS)) {
                    logger.warn("[Reminder Timeout] Some reminders didn't complete in time");
                }

                logger.info("[Reminder Complete] Event: {} | Successful: {} | Failed: {}",
                        event.getEventId(),
                        successfulNotifications.size(),
                        failedNotifications.size());

                notificationLog.computeIfPresent(event, (k, v) -> {
                    v.addAll(successfulNotifications.stream()
                            .map(name -> "[Reminder] " + name)
                            .toList());
                    return v;
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("[Reminder Interrupted] Reminders cancelled for event: {}",
                        event.getEventId());
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
