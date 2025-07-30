//package org.example.notifications.subscribers;
//
//import org.example.notifications.events.Event;
//import org.example.notifications.subscribers.AbstractSubscriber;
//import org.example.notifications.subscribers.Subscriber;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//public class User extends AbstractSubscriber<Event> {
//    private static final List<Subscriber> strategies = new ArrayList<>();
//
//    public User(String name) {
//        super(name);
//    }
//
//    public void addStrategy(Subscriber strategy) {
//        strategies.add(strategy);
//    }
//
//    @Override
//    public  String notify(Event event) {
//        return strategies.stream()
//                .filter(s -> s.getFilter().test(event))
//                .map(s -> getName() + " received: " + s.formatNotification(event))
//                .collect(Collectors.joining("\n"));
//    }
//
//    @Override
//    public Predicate<Event> getFilter() {
//        return event -> strategies.stream().anyMatch(s -> s.getFilter().test(event));
//    }
//}
