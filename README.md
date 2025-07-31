# 📢 Event-Driven-In-Memory-Notification-System

A modular, event-driven in-memory notification system built in Java. The system allows flexible subscriber strategies (based on priority, time, event type, etc.), delayed and scheduled event publishing, and provides full logging and reporting.

---

## 🧩 System Design Overview

### 🗂️ Core Components

- **Events**  
  Represent activities in the system with timestamp, type, priority, and payload.
    - `NewTaskEvent`, `TimeEvent` (extends `AbstractEvent`)
    - Enum: `Priority { LOW, MEDIUM, HIGH }`

- **Subscribers**  
  Receive and filter events based on criteria.
    - `AllEventsSubscriber`, `PrioritySubscriber`, `TimeWindowSubscriber`, `TaskOnlySubscriber`
    - `CompositeSubscriber` supports combining multiple filters.
    - `User` dynamically composes multiple subscriber strategies.

- **EventBus**  
  Central system for event publishing and subscriber notification.
    - Supports filtering and delayed dispatch.
    - Maintains event history.

- **Publishers**
    - `TaskPublisher`: creates `NewTaskEvent`
    - `ReminderPublisher`: emits `TimeEvent` at fixed intervals

- **Scheduler**
    - `EventScheduler`: manages scheduling and thread execution

- **Logger**
    - `EventLogger`: logs events, filters by type/priority, and provides summaries.

---

## 🚀 How to Run the App

### ✅ Prerequisites

- Java 17 or later
- Maven
- IDE (IntelliJ, Eclipse, etc.)

### 📁 Project Structure

```
src/
├── main/java/org/example/notifications/
│   ├── centralsystem/
│   ├── events/
│   ├── logger/
│   ├── publishers/
│   ├── scheduler/
│   ├── subscribers/
│   ├── util/
│   └── Main.java
|
├── main/java/org/example/notifications/
    ├── MainTest/
```

### ▶️ Run the Application

#### 1. Clone the Project
```bash
git clone https://github.com/Mir-Tasleem/Event-Driven-In-Memory-Notification-System.git
cd Event-Driven-In-Memory-Notification-System
```

#### 2. Compile and Run
```bash
mvn clean package
java -jar target/Event-Driven-Notification-System-1.0-SNAPSHOT-jar-with-dependencies.jar
```
Or from your IDE, run the `Main` class directly.

#### 3. View Output
All logs and subscriber notifications are printed to the console, including delayed and filtered notifications.

---


## 🧪 Run the Tests

### ✅ Prerequisites

Make sure your `pom.xml` already includes the JUnit dependency:

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
````

> ✅ If you’ve already pushed `pom.xml` with this dependency, you’re good to go.

---

### ▶️ Execute the Tests

Run all unit tests using Maven:

```bash
mvn test
```

Test results will appear in the console, and detailed reports are stored in:

```
target/surefire-reports/
```

You can open these reports in any text editor or IDE to review test output and errors (if any).

---


## 🧠 Key Features

- ✅ Real-time and scheduled event publishing
- ✅ Dynamic subscriber filtering strategies
- ✅ Delayed event notifications (e.g., 5 minutes before a task)
- ✅ In-memory logging and reporting
- ✅ Thread-safe, modular design

---

## 🧪 Simulated User Roles

| Role           | Subscriptions                                |
|----------------|-----------------------------------------------|
| SystemMonitor  | All Events                                    |
| TaskManager    | Task Events Only                              |
| ProjectManager | High Priority + Task Events                   |
| TeamLead       | Medium Priority + TimeWindow (8am–6pm)        |
| Developer      | Task Events Only                              |
| Head           | All Events                                    |

---

## 📊 Reporting & Shutdown

After a 60-second simulation:
- Logs are printed for received events
- Summary of events by type and priority
- Graceful shutdown of threads via shutdown hooks

