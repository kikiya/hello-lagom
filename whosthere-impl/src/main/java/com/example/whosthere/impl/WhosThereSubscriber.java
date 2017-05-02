package com.example.whosthere.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import com.example.greeting.api.GreetingEvent;
import com.example.greeting.api.GreetingService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Created by kiki on 4/26/17.
 */
@Singleton
public class WhosThereSubscriber {


    @Inject
    public WhosThereSubscriber(GreetingService greetingService, WhosThereRepository repository) {

        //Create a subscriber
        greetingService.helloEvents().subscribe()
                // And subscribe to it with at least once processing semantics.
                .atLeastOnce(
                        // Create a flow that emits a Done for each message it processes
                        Flow.<GreetingEvent>create().mapAsync(1, event -> {

                            if (event instanceof GreetingEvent.GreetingMessageChanged) {
                                GreetingEvent.GreetingMessageChanged messageChanged = (GreetingEvent.GreetingMessageChanged) event;
                                // Update the message
                                System.out.println("****************** the event to add looks like: " + messageChanged.toString());
                                return repository.addGuest(messageChanged.name, messageChanged.message);

                            } else {
                                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&& ignoring event " + event);
                                // Ignore all other events
                                return completedFuture(Done.getInstance());
                            }
                        })
                );

    }
}