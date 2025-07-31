package org.example.notifications;

import org.example.notifications.centralsystem.EventBus;
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

        // Create EventBus and Logger
        EventBus<Event> eventBus = new EventBus<>(eventLogger, 5);


        eventBus.registerSubscriber(new AllEventsSubscriber("SM","SystemMonitor"));
        eventBus.registerSubscriber(new TaskOnlySubscriber("TM","TaskManager"));
        eventBus.registerSubscriber(new TimeWindowSubscriber("BHM","BusinessHoursMonitor", 9, 17));

        // User-based composite subscribers
        User projectManager = new User("Project Manager");
        projectManager.addSubscription(SubscriberType.PRIORITY, Priority.HIGH);
        projectManager.addSubscription(SubscriberType.TASK_ONLY);

        User teamLead = new User( "Team Lead");
        teamLead.addSubscription(SubscriberType.PRIORITY, Priority.MEDIUM);
        teamLead.addSubscription(SubscriberType.TIME_WINDOW, 8, 18);

        User developer = new User("Developer");
        developer.addSubscription(SubscriberType.TASK_ONLY);


        User head = new User("Head");
        head.addSubscription(SubscriberType.ALL_EVENTS);

        // Register all users at once
        eventBus.registerMultipleUsers(List.of(projectManager, teamLead, developer));

        //Register Single Uer
        eventBus.registerSubscriber(head.getSubscriber());

        // Start Publishers
        TaskPublisher taskPublisher1 = new TaskPublisher(eventBus);
        TaskPublisher taskPublisher2 = new TaskPublisher(eventBus);

        LocalDateTime projectReportTime = LocalDateTime.now().plusSeconds(15); // 15 seconds from now
        LocalDateTime meetingTime = LocalDateTime.now().plusMinutes(6); // 6 minutes from now

        taskPublisher1.publishNewTask("Prepare project report", Priority.HIGH, projectReportTime);
        taskPublisher1.publishNewTask("Client meeting at 4pm", Priority.MEDIUM, meetingTime);

        LocalDateTime finalMeet = LocalDateTime.now().plusSeconds(330); // 330 seconds from now
        LocalDateTime projectSubmission = LocalDateTime.now().plusMinutes(6); // 6 minutes from now

        taskPublisher2.publishNewTask("Final Project Meeting", Priority.HIGH, finalMeet);
        taskPublisher2.publishNewTask("Project Submission", Priority.MEDIUM, projectSubmission);

        // Start Event Scheduler to simulate TimeEvent every 10 seconds
        EventScheduler scheduler = new EventScheduler(eventBus, 10);
        ScheduledExecutorService schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.scheduleAtFixedRate(scheduler, 0, 10, TimeUnit.SECONDS);

        // Let the system run for 1 minute
        Thread.sleep(60000);

        // Shutdown everything
        schedulerService.shutdown();
        eventBus.shutdown();

        // Print Logs and Reports
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