package com.example.whosthere.impl;

import akka.Done;
import akka.stream.javadsl.Flow;
import com.example.hello.api.GreetingMessage;
import com.example.hello.api.HelloEvent;
import com.example.hello.api.HelloService;

import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Created by kiki on 4/26/17.
 */
@Singleton
public class WhosThereSubscriber {


    @Inject
    public WhosThereSubscriber(HelloService helloService, WhosThereRepository repository) {

        //#subscribe-to-topic
        helloService.greetingsTopic()
                .subscribe() // <-- you get back a Subscriber instance
                .atLeastOnce(Flow.fromFunction((GreetingMessage message) -> {
                    repository.addGuest(message.id, message.message);
                    return Done.getInstance();
                }));

        // Create a subscriber
//        helloService.helloEvents().subscribe()
//                // And subscribe to it with at least once processing semantics.
//                .atLeastOnce(
//                        // Create a flow that emits a Done for each message it processes
//                        Flow.<HelloEvent>create().mapAsync(1, event -> {
//
//                            if (event instanceof HelloEvent.GreetingMessageChanged) {
//                                HelloEvent.GreetingMessageChanged messageChanged = (HelloEvent.GreetingMessageChanged) event;
//                                // Update the message
//                                System.out.println("****************** the event to add looks like: " + messageChanged.toString());
//                                return repository.addGuest(messageChanged.id, messageChanged.message);
//
//                            } else {
//                                System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&& ignoring event " + event);
//                                // Ignore all other events
//                                return completedFuture(Done.getInstance());
//                            }
//                        })
//                );
//
    }
}
