package org.example.notifications;

import org.example.notifications.centralSystem.EventBus;
import org.example.notifications.logger.EventLogger;
import org.example.notifications.events.NewTaskEvent;
import org.example.notifications.events.Priority;
import org.example.notifications.publishers.ReminderPublisher;
import org.example.notifications.publishers.TaskPublisher;
import org.example.notifications.scheduler.EventScheduler;
import org.example.notifications.subscribers.AllEventsSubscriber;
import org.example.notifications.subscribers.TaskOnlySubscriber;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        EventLogger eventLogger = new EventLogger();

        // Step 1: Create EventBus and Logger
        EventBus eventBus = new EventBus(eventLogger,5);

        // Step 2: Register Subscribers
        eventBus.registerSubscriber(new AllEventsSubscriber("AllSubscriber"));
        eventBus.registerSubscriber(new TaskOnlySubscriber("TaskSubscriber"));

        // Step 3: Start Publishers
        TaskPublisher taskPublisher = new TaskPublisher(eventBus);
        ReminderPublisher reminderPublisher = new ReminderPublisher(eventBus,3000);
        Thread reminderThread=new Thread(reminderPublisher);

        taskPublisher.publishNewTask("TASK-101", "Prepare project report", Priority.HIGH);
        taskPublisher.publishNewTask("TASK-102", "Client meeting at 4pm", Priority.MEDIUM);

        reminderThread.start();
        reminderThread.stop();

        // Step 4: Start Event Scheduler to simulate TimeEvent every 10 seconds
        EventScheduler scheduler = new EventScheduler(eventBus, 10);
        ScheduledExecutorService schedulerService = Executors.newSingleThreadScheduledExecutor();
        schedulerService.scheduleAtFixedRate(scheduler, 0, 10, TimeUnit.SECONDS);

        // Let system run for a while (e.g., 30 seconds)
        Thread.sleep(30000);

        // Step 5: Shutdown everything
        schedulerService.shutdown();
        eventBus.shutdown();

        // Step 6: Print Logs and Reports
        System.out.println("===== EVENT LOGS =====");
        eventLogger.printSummaryReport();
        eventLogger.printRecentEvents(10);
        eventLogger.printEventsByPriority(Priority.HIGH);
//        eventLogger.printGroupedByType();
    }
}
