package org.example.notifications.logger;

import org.example.notifications.events.Event;
import org.example.notifications.events.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EventLogger {

    private static final Logger logger = LoggerFactory.getLogger(EventLogger.class);
    private final List<Event> eventLog = new CopyOnWriteArrayList<>();

    // Log a new event
    public void logEvent(Event event) {
        eventLog.add(event);
    }

    // Get all logged events
    public List<Event> getAllEvents() {
        return new ArrayList<>(eventLog);
    }

    // Filter events by event type
    public List<Event> getEventsByType(String type) {
        return eventLog.stream()
                .filter(e -> e.getEventType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // Group events by type
    public Map<String, List<Event>> groupEventsByType() {
        return eventLog.stream()
                .collect(Collectors.groupingBy(Event::getEventType));
    }

    // Filter events by priority
    public List<Event> getEventsByPriority(Priority priority) {
        return eventLog.stream()
                .filter(e -> priority.equals(e.getPriority()))
                .collect(Collectors.toList());
    }

    // Get events created in the last N minutes
    public List<Event> getEventsInLastNMinutes(int minutes) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);
        return eventLog.stream()
                .filter(e -> e.getTimeStamp().isAfter(cutoff))
                .collect(Collectors.toList());
    }

    // Count total number of events
    public int getTotalEventCount() {
        return eventLog.size();
    }

    // ----------------- Reporting Methods -----------------

    public void printSummaryReport() {
        logger.info("===== Event Summary Report =====");
        logger.info("Total Events Logged: {}", getTotalEventCount());
        logger.info("Events by Type:");
        groupEventsByType().forEach((type, events) ->
                logger.info("  - {}: {}", type, events.size()));
    }

    public void printRecentEvents(int minutes) {
        logger.info("===== Events in Last {} Minutes =====", minutes);
        List<Event> recent = getEventsInLastNMinutes(minutes);
        if (recent.isEmpty()) {
            logger.info("No recent events found.");
        } else {
            recent.forEach(event ->
                    logger.info("{} | {} | {}", event.getTimeStamp(), event.getEventType(), event.getPayload()));
        }
    }

    public void printEventsByPriority(Priority priority) {
        logger.info("===== Events with Priority: {} =====", priority);
        List<Event> list = getEventsByPriority(priority);
        if (list.isEmpty()) {
            logger.info("No events found with priority {}", priority);
        } else {
            list.forEach(event ->
                    logger.info("{} | {} | {}", event.getTimeStamp(), event.getEventType(), event.getPayload()));
        }
    }
}
