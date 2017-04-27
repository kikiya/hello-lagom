/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.hello.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.example.hello.api.GreetingMessage;
import com.example.hello.api.HelloService;
import com.example.hello.impl.HelloCommand.Hello;
import com.example.hello.impl.HelloCommand.UseGreetingMessage;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
import com.lightbend.lagom.javadsl.persistence.Offset;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Implementation of the HelloService.
 */
public class HelloServiceImpl implements HelloService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession db;

    @Inject
    public HelloServiceImpl(PersistentEntityRegistry persistentEntityRegistry, ReadSide readSide, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.db = db;

        persistentEntityRegistry.register(HelloEntity.class);
        readSide.register(HelloEventProcessor.class);
    }

    @Override
    public ServiceCall<NotUsed, String> hello(String id) {
        return request -> {
            // Look up the hello world entity for the given ID.
            PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
            // Ask the entity the Hello command.
            return ref.ask(new Hello(id));
        };
    }

    @Override
    public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
        System.out.println("************** useGreeting ID: " + id);
        return request -> {
            // Look up the hello world entity for the given ID.
            PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
            // Tell the entity to use the greeting message specified.
            return ref.ask(new UseGreetingMessage(request.message));
        };

    }

    @Override
    public ServiceCall<NotUsed, PSequence<String>> getGreetings(String userId) {
        System.out.println("*************************** " + "in getGreetings");
        return req -> {
            CompletionStage<PSequence<String>> result = db.selectAll("SELECT * FROM greeting WHERE userId = ?", userId)
                    .thenApply(rows -> {
                        List<String> followers = rows.stream().map(row -> row.getString("message")).collect(Collectors.toList());
                        return TreePVector.from(followers);
                    });
            return result;
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<String>> getAllGreetings() {
        System.out.println("*************************** " + "in getAllGreetings");
        return req -> {
            CompletionStage<PSequence<String>> result = db.selectAll("SELECT * FROM greeting")
                    .thenApply(rows -> {
                        List<String> followers = rows.stream().map(row -> row.getString("userid") + "-" + row.getString("message")).collect(Collectors.toList());
                        return TreePVector.from(followers);
                    });
            return result;
        };
    }

    private Pair<GreetingMessage, Offset> convertEvent(Pair<HelloEvent, Offset> pair) {
        return new Pair<>(new GreetingMessage(((HelloEvent.GreetingMessageChanged)pair.first()).name, ((HelloEvent.GreetingMessageChanged)pair.first()).message), pair.second());
    }

    @Override
    public Topic<com.example.hello.api.HelloEvent> helloEvents() {
        // We want to publish all the shards of the hello event
        System.out.println("*********************** in helloEvents in impl");
        return TopicProducer.taggedStreamWithOffset(HelloEventTag.INSTANCE.allTags(), (tag, offset) ->

                // Load the event stream for the passed in shard tag
                persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {

                    System.out.println("********************** i am publishing");

                    // Now we want to convert from the persisted event to the published event.
                    // Although these two events are currently identical, in future they may
                    // change and need to evolve separately, by separating them now we save
                    // a lot of potential trouble in future.
                    com.example.hello.api.HelloEvent eventToPublish;

                    if (eventAndOffset.first() instanceof HelloEvent.GreetingMessageChanged) {
                        HelloEvent.GreetingMessageChanged messageChanged = (HelloEvent.GreetingMessageChanged) eventAndOffset.first();
                        eventToPublish = new com.example.hello.api.HelloEvent.GreetingMessageChanged(
                                messageChanged.name, messageChanged.message
                        );
                    } else {
                        throw new IllegalArgumentException("Unknown event: " + eventAndOffset.first());
                    }

                    // We return a pair of the translated event, and its offset, so that
                    // Lagom can track which offsets have been published.
                    return Pair.create(eventToPublish, eventAndOffset.second());
                })
        );
    }
}
