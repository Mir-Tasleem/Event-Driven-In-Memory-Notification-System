package org.example.notifications;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.Event;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.events.Priority;
import org.example.notifications.publishers.TaskPublisher;
import org.example.notifications.scheduler.EventScheduler;
import org.example.notifications.subscribers.AllEventsSubscriber;
import org.example.notifications.subscribers.PrioritySubscriber;
import org.example.notifications.subscribers.TaskOnlySubscriber;
import org.example.notifications.subscribers.TimeWindowSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main{
    public static void main(String[] args) throws InterruptedException {
        Logger logger= LoggerFactory.getLogger(Main.class);
        EventLogger eventLogger = new EventLogger();

        // Step 1: Create EventBus and Logger
        EventBus<Event> eventBus = new EventBus<>(eventLogger,5);

        // Step 2: Register Subscribers
        eventBus.registerSubscriber(new AllEventsSubscriber("AllEventSubscriber"));
        eventBus.registerSubscriber(new TaskOnlySubscriber("TaskSubscriber A"));
        eventBus.registerSubscriber(new TaskOnlySubscriber("TaskSubscriber B"));
        eventBus.registerSubscriber(new TimeWindowSubscriber("TimeWindowSubscriber",9,17));
        eventBus.registerSubscriber(new PrioritySubscriber("HighPrioritySubscriber",Priority.HIGH));

        // Step 3: Start Publishers
        TaskPublisher taskPublisher = new TaskPublisher(eventBus);
        LocalDateTime projectReportTime = LocalDateTime.of(2025, 7, 30, 12, 3);
        LocalDateTime meetingTime = LocalDateTime.of(2025, 7, 30, 16, 0);
        taskPublisher.publishNewTask("Prepare project report", Priority.HIGH,projectReportTime);
        taskPublisher.publishNewTask("Client meeting at 4pm", Priority.MEDIUM, meetingTime);

        //Step 4: Start Event Scheduler to simulate TimeEvent every 10 seconds
        EventScheduler scheduler = new EventScheduler(eventBus, 10);
        ScheduledExecutorService schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.scheduleAtFixedRate(scheduler, 0, 10, TimeUnit.SECONDS);

        Thread.sleep(6000);

        // Step 5: Shutdown everything
        schedulerService.shutdown();
        eventBus.shutdown();

        // Step 6: Print Logs and Reports
        logger.info("===== EVENT LOGS =====");
        eventLogger.printSummaryReport();
        eventLogger.getAllEvents();
        eventLogger.printRecentEvents(60);
        eventLogger.printEventsByType("NewTaskEvent");
        eventLogger.printEventsByPriority(Priority.HIGH);
        eventLogger.printEventsByPriority(Priority.MEDIUM);
        eventLogger.printEventsByPriority(Priority.LOW);

    }
}
