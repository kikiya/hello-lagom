package com.example.whosthere.impl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Flow;
import com.example.hello.api.GreetingMessage;
import com.example.hello.api.HelloEvent;
import com.example.hello.api.HelloService;
import com.example.whosthere.api.WhosThereService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Created by kiki on 4/26/17.
 */
@Singleton
public class WhosThereSubscriber {

//    Topic<ItemEvent> itemEventTopic = itemService.itemEvents();
//    Topic<BidEvent> bidEventTopic = biddingService.bidEvents();
//        itemEventTopic.subscribe().atLeastOnce(Flow.<ItemEvent>create().map(this::toDocument).mapAsync(1, indexedStore::store));
//        bidEventTopic.subscribe().atLeastOnce(Flow.<BidEvent>create().map(this::toDocument).mapAsync(1, indexedStore::store));

    @Inject
    public WhosThereSubscriber(HelloService helloService, WhosThereRepository repository) {

        System.out.println("****************** i am subscribing");
        //#subscribe-to-topic
        helloService.greetingsTopic()
                .subscribe() // <-- you get back a Subscriber instance
                .atLeastOnce(Flow.fromFunction((GreetingMessage message) -> {
                    repository.addGuest(message.id, message.message);
//                    repository.addGuest(message.id, message.message);
                    return Done.getInstance();
                }));
        //#subscribe-to-topic
//        return name -> completedFuture(Done.getInstance());

        // Create a subscriber
//        helloService.greetingsTopic().subscribe()
//                // And subscribe to it with at least once processing semantics.
//                .atLeastOnce(
//                        // Create a flow that emits a Done for each message it processes
//                        Flow.<HelloEvent>create().mapAsync(1, event -> {
//                            System.out.println("***************** in WHOS_THERE_SUBSCRIBER");
//
//                            if (event instanceof HelloEvent.GreetingMessageChanged) {
//                                HelloEvent.GreetingMessageChanged messageChanged = (HelloEvent.GreetingMessageChanged) event;
//                                // Update the message
//                                return repository.addGuest(messageChanged.getName(), messageChanged.getMessage());
//
//                            } else {
//                                // Ignore all other events
//                                return completedFuture(Done.getInstance());
//                            }
//                        })
//                );

    }
}
