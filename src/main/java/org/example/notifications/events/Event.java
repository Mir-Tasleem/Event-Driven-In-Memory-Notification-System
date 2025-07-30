package org.example.notifications.events;

import java.time.LocalDateTime;

public interface Event {
    String getEventId();
    String getEventType();
    LocalDateTime getTimeStamp();
    String getPayload();
    Priority getPriority();
}