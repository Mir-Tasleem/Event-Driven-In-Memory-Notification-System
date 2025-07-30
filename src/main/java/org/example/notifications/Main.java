package org.example.notifications;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.events.Event;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.events.Priority;
import org.example.notifications.publishers.TaskPublisher;
import org.example.notifications.scheduler.EventScheduler;
import org.example.notifications.subscribers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        EventLogger eventLogger = new EventLogger();

        // Step 1: Create EventBus and Logger
        EventBus<Event> eventBus = new EventBus<>(eventLogger, 5);

        // Step 2: Register Subscribers (both old and new ways)

        // Traditional individual subscribers (existing functionality)
        eventBus.registerSubscriber(new AllEventsSubscriber("SystemMonitor"));
        eventBus.registerSubscriber(new TaskOnlySubscriber("TaskManager"));
        eventBus.registerSubscriber(new TimeWindowSubscriber("BusinessHoursMonitor", 9, 17));

        // New User-based composite subscribers
        User projectManager = new User("pm1", "Project Manager");
        projectManager.addSubscription(SubscriberType.PRIORITY, Priority.HIGH);
        projectManager.addSubscription(SubscriberType.TASK_ONLY);

        User teamLead = new User("tl1", "Team Lead");
        teamLead.addSubscription(SubscriberType.PRIORITY, Priority.MEDIUM);
        teamLead.addSubscription(SubscriberType.TIME_WINDOW, 8, 18);

        User developer = new User("dev1", "Developer");
        developer.addSubscription(SubscriberType.TASK_ONLY);

        // Register all users at once
        eventBus.registerMultipleUsers(List.of(projectManager, teamLead, developer));

        // Step 3: Start Publishers
        TaskPublisher taskPublisher = new TaskPublisher(eventBus);
        LocalDateTime projectReportTime = LocalDateTime.now().plusSeconds(15); // 15 seconds from now
        LocalDateTime meetingTime = LocalDateTime.now().plusSeconds(30); // 30 seconds from now

        taskPublisher.publishNewTask("Prepare project report", Priority.HIGH, projectReportTime);
        taskPublisher.publishNewTask("Client meeting at 4pm", Priority.MEDIUM, meetingTime);

        // Step 4: Start Event Scheduler to simulate TimeEvent every 10 seconds
        EventScheduler scheduler = new EventScheduler(eventBus, 10);
        ScheduledExecutorService schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.scheduleAtFixedRate(scheduler, 0, 10, TimeUnit.SECONDS);

        // Let the system run for 1 minute
        Thread.sleep(1000);

        // Step 5: Shutdown everything
        schedulerService.shutdown();
        eventBus.shutdown();

        // Step 6: Print Logs and Reports
        printSystemReports(eventLogger);
    }

    private static void printSystemReports(EventLogger eventLogger) {
        logger.info("\n===== SYSTEM SUMMARY =====");
        eventLogger.printSummaryReport();

        logger.info("\n===== EVENT TYPE ANALYSIS =====");
        eventLogger.printEventsByType("NewTaskEvent");
        eventLogger.printEventsByType("TimeEvent");

        logger.info("\n===== PRIORITY ANALYSIS =====");
        eventLogger.printEventsByPriority(Priority.HIGH);
        eventLogger.printEventsByPriority(Priority.MEDIUM);
        eventLogger.printEventsByPriority(Priority.LOW);

        logger.info("\n===== RECENT ACTIVITY =====");
        eventLogger.printRecentEvents(60);

        logger.info("\n===== COMPLETE EVENT LOG =====");
        eventLogger.getAllEvents().forEach(event -> logger.info("{}",
                event.getPayload()));
    }
}