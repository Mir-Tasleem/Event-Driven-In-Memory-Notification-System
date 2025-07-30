package org.example.notifications.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    // Separate counters for different entity types
    private static final AtomicLong EVENT_ID = new AtomicLong(1000);
    private static final AtomicLong PUBLISHER_ID = new AtomicLong(2000);
    private static final AtomicLong SUBSCRIBER_ID = new AtomicLong(3000);

    // Prefixes for human-readable IDs
    private static final String EVENT_PREFIX = "EVT-";
    private static final String PUBLISHER_PREFIX = "PUB-";
    private static final String SUBSCRIBER_PREFIX = "SUB-";

    // Private constructor to prevent instantiation
    private IdGenerator() {}


    public static String generateEventId() {
        return EVENT_PREFIX + EVENT_ID.incrementAndGet();
    }


    public static String generatePublisherId() {
        return PUBLISHER_PREFIX + PUBLISHER_ID.incrementAndGet();
    }


    public static String generateSubscriberId() {
        return SUBSCRIBER_PREFIX + SUBSCRIBER_ID.incrementAndGet();
    }
}