package org.example.notifications;

import org.example.notifications.centralsystem.EventBus;
import org.example.notifications.events.Event;
import org.example.notifications.events.NewTaskEvent;
import org.example.notifications.events.Priority;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.subscribers.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private EventBus<Event> eventBus;
    private EventLogger eventLogger;

    @BeforeEach
    void setUp() {
        eventLogger = new EventLogger();
        eventBus = new EventBus<>(eventLogger, 3);
    }

    // Helper method to generate test IDs
    private String testId() {
        return "test-" + System.currentTimeMillis() + "-" + Math.random();
    }

    // Existing tests for individual subscribers
    @Test
    void testAllEventsSubscriberReceivesAllEvents() throws InterruptedException {
        AllEventsSubscriber subscriber = new AllEventsSubscriber(testId(), "AllSub");
        eventBus.registerSubscriber(subscriber);

        NewTaskEvent event = new NewTaskEvent("Task 1", Priority.LOW, LocalDateTime.now());
        eventBus.publishEvent(event);

        Thread.sleep(200);
        assertNotificationReceived(event, "AllEventsSubscriber: AllSub");
    }

    @Test
    void testPrioritySubscriberOnlyReceivesMatchingPriority() throws InterruptedException {
        PrioritySubscriber highPrioritySub = new PrioritySubscriber(testId(), "HighOnly", Priority.HIGH);
        eventBus.registerSubscriber(highPrioritySub);

        NewTaskEvent highEvent = new NewTaskEvent("Urgent", Priority.HIGH, LocalDateTime.now());
        NewTaskEvent lowEvent = new NewTaskEvent("Optional", Priority.LOW, LocalDateTime.now());

        eventBus.publishEvent(highEvent);
        eventBus.publishEvent(lowEvent);

        Thread.sleep(300);
        assertNotificationReceived(highEvent, "PrioritySubscriber: HighOnly");
        assertNotificationNotReceived(lowEvent, "PrioritySubscriber: HighOnly");
    }

    @Test
    void testTaskOnlySubscriberReceivesOnlyTaskEvents() throws InterruptedException {
        TaskOnlySubscriber taskSub = new TaskOnlySubscriber(testId(), "TaskOnly");
        eventBus.registerSubscriber(taskSub);

        NewTaskEvent taskEvent = new NewTaskEvent("Report", Priority.MEDIUM, LocalDateTime.now());
        eventBus.publishEvent(taskEvent);

        Thread.sleep(200);
        assertNotificationReceived(taskEvent, "TaskOnlySubscriber: TaskOnly");
    }

    @Test
    void testTimeWindowSubscriberFiltersByHour() throws InterruptedException {
        int nowHour = LocalDateTime.now().getHour();
        TimeWindowSubscriber sub = new TimeWindowSubscriber(testId(), "TimeFilter", nowHour - 1, nowHour + 1);
        eventBus.registerSubscriber(sub);

        NewTaskEvent taskEvent = new NewTaskEvent("Windowed", Priority.LOW, LocalDateTime.now());
        eventBus.publishEvent(taskEvent);

        Thread.sleep(200);
        assertNotificationReceived(taskEvent, "TimeWindowSubscriber: TimeFilter");
    }

    // New tests for User and CompositeSubscriber functionality
    @Test
    void testUserWithMultipleSubscriptions() throws InterruptedException {
        User user = new User("TestUser");
        user.addSubscription(SubscriberType.PRIORITY, Priority.HIGH);
        user.addSubscription(SubscriberType.TASK_ONLY);
        eventBus.registerUserSubscriptions(user);

        NewTaskEvent highPriorityTask = new NewTaskEvent("Important", Priority.HIGH, LocalDateTime.now());
        NewTaskEvent lowPriorityTask = new NewTaskEvent("Trivial", Priority.LOW, LocalDateTime.now());

        eventBus.publishEvent(highPriorityTask);
        eventBus.publishEvent(lowPriorityTask);

        Thread.sleep(300);
        assertNotificationReceived(highPriorityTask, "CompositeSubscriber: TestUser");
        assertNotificationNotReceived(lowPriorityTask, "CompositeSubscriber: TestUser");
    }

    @Test
    void testMultipleUsersWithDifferentPreferences() throws InterruptedException {
        User manager = new User("Manager");
        manager.addSubscription(SubscriberType.PRIORITY, Priority.HIGH);

        User developer = new User("Developer");
        developer.addSubscription(SubscriberType.TASK_ONLY);

        eventBus.registerMultipleUsers(List.of(manager, developer));

        NewTaskEvent event = new NewTaskEvent("Task", Priority.HIGH, LocalDateTime.now());
        eventBus.publishEvent(event);

        Thread.sleep(300);
        assertNotificationReceived(event, "CompositeSubscriber: Manager");
        assertNotificationReceived(event, "CompositeSubscriber: Developer");
    }

    @Test
    void testEventHistoryAndCounts() throws InterruptedException {
        NewTaskEvent event1 = new NewTaskEvent("E1", Priority.HIGH, LocalDateTime.now());
        NewTaskEvent event2 = new NewTaskEvent("E2", Priority.MEDIUM, LocalDateTime.now());

        eventBus.publishEvent(event1);
        eventBus.publishEvent(event2);

        Thread.sleep(200);
        assertEquals(2, eventBus.getEventHistory().size());
        assertEquals(2, eventBus.getTotalPublishedEvents());
    }

    @Test
    void testMultiplePublishersConcurrently() throws InterruptedException {
        // Register subscribers
        eventBus.registerSubscriber(new AllEventsSubscriber(testId(), "AllEventsMonitor"));

        // Create multiple publishers
        int numberOfPublishers = 5;
        int eventsPerPublisher = 10;
        ExecutorService publisherPool = Executors.newFixedThreadPool(numberOfPublishers);
        CountDownLatch latch = new CountDownLatch(numberOfPublishers * eventsPerPublisher);

        // Track expected events
        List<NewTaskEvent> expectedEvents = new CopyOnWriteArrayList<>();

        // Start publishers
        for (int i = 0; i < numberOfPublishers; i++) {
            final int publisherId = i;
            publisherPool.submit(() -> {
                for (int j = 0; j < eventsPerPublisher; j++) {
                    NewTaskEvent event = new NewTaskEvent(
                            "Task from publisher " + publisherId + "-" + j,
                            Priority.values()[j % Priority.values().length],
                            LocalDateTime.now().plusMinutes(j)
                    );
                    expectedEvents.add(event);
                    eventBus.publishEvent(event);
                    latch.countDown();
                    try {
                        Thread.sleep(10); // Small delay between events
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        // Wait for all events to be published
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Not all events were published in time");
        publisherPool.shutdown();

        // Allow some time for processing
        Thread.sleep(500);

        // Verify all events were processed
        assertEquals(numberOfPublishers * eventsPerPublisher, eventBus.getTotalPublishedEvents());

        // Verify each event was logged
        Map<Event, List<String>> notificationLog = eventBus.getNotificationLog();
        for (NewTaskEvent expectedEvent : expectedEvents) {
            assertTrue(notificationLog.containsKey(expectedEvent),
                    "Event not processed: " + expectedEvent.getPayload());
            assertTrue(notificationLog.get(expectedEvent).contains("AllEventsSubscriber: AllEventsMonitor"),
                    "Subscriber not notified for event: " + expectedEvent.getPayload());
        }
    }


    // Helper assertion methods
    private void assertNotificationReceived(Event event, String expectedSubscriberInfo) {
        Map<Event, List<String>> log = eventBus.getNotificationLog();
        assertTrue(log.containsKey(event), "Event not found in log");
        assertTrue(log.get(event).contains(expectedSubscriberInfo),
                "Subscriber " + expectedSubscriberInfo + " not notified");
    }

    private void assertNotificationNotReceived(Event event, String unexpectedSubscriberInfo) {
        Map<Event, List<String>> log = eventBus.getNotificationLog();
        if (log.containsKey(event)) {
            assertFalse(log.get(event).contains(unexpectedSubscriberInfo),
                    "Subscriber " + unexpectedSubscriberInfo + " was unexpectedly notified");
        }
    }

    @AfterEach
    void tearDown() {
        eventBus.shutdownNow();
    }
}