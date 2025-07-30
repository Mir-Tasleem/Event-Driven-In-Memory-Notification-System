package org.example.notifications;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.Event;
import org.example.notifications.events.NewTaskEvent;
import org.example.notifications.events.Priority;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.subscribers.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private EventBus<Event> eventBus;
    private EventLogger eventLogger;

    @BeforeEach
    void setUp() {
        eventLogger = new EventLogger();
        eventBus = new EventBus<Event>(eventLogger, 3);
    }

    @Test
    void testAllEventsSubscriberReceivesAllEvents() throws InterruptedException {
        AllEventsSubscriber subscriber = new AllEventsSubscriber("AllSub");
        eventBus.registerSubscriber(subscriber);

        NewTaskEvent event = new NewTaskEvent("Task 1", Priority.LOW, LocalDateTime.now());
        eventBus.publishEvent(event);

        Thread.sleep(200); // allow async delivery

        Map<Event, List<String>> log = eventBus.getNotificationLog();
        assertTrue(log.containsKey(event));
        assertTrue(log.get(event).contains("AllSub"));
    }

    @Test
    void testPrioritySubscriberOnlyReceivesMatchingPriority() throws InterruptedException {
        PrioritySubscriber highPrioritySub = new PrioritySubscriber("HighOnly", Priority.HIGH);
        eventBus.registerSubscriber(highPrioritySub);

        NewTaskEvent highEvent = new NewTaskEvent("Urgent", Priority.HIGH, LocalDateTime.now());
        NewTaskEvent lowEvent = new NewTaskEvent("Optional", Priority.LOW, LocalDateTime.now());

        eventBus.publishEvent(highEvent);
        eventBus.publishEvent(lowEvent);

        Thread.sleep(300);

        Map<Event, List<String>> log = eventBus.getNotificationLog();
        assertTrue(log.get(highEvent).contains("HighOnly"));
        assertFalse(log.getOrDefault(lowEvent, List.of()).contains("HighOnly"));
    }

    @Test
    void testTaskOnlySubscriberReceivesOnlyTaskEvents() throws InterruptedException {
        TaskOnlySubscriber taskSub = new TaskOnlySubscriber("TaskOnly");
        eventBus.registerSubscriber(taskSub);

        NewTaskEvent taskEvent = new NewTaskEvent("Report", Priority.MEDIUM, LocalDateTime.now());
        eventBus.publishEvent(taskEvent);

        Thread.sleep(200);

        Map<Event, List<String>> log = eventBus.getNotificationLog();
        assertTrue(log.get(taskEvent).contains("TaskOnly"));
    }

    @Test
    void testTimeWindowSubscriberFiltersByHour() throws InterruptedException {
        int nowHour = LocalDateTime.now().getHour();
        TimeWindowSubscriber sub = new TimeWindowSubscriber("TimeFilter", nowHour - 1, nowHour + 1);
        eventBus.registerSubscriber(sub);

        NewTaskEvent taskEvent = new NewTaskEvent("Windowed", Priority.LOW, LocalDateTime.now());
        eventBus.publishEvent(taskEvent);

        Thread.sleep(200);

        assertTrue(eventBus.getNotificationLog().get(taskEvent).contains("TimeFilter"));
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

    @AfterEach
    void tearDown() {
        eventBus.shutdownNow();
    }
}
