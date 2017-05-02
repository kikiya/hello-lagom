/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greeting.impl;

import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.example.greeting.api.GreetingMessage;
import com.example.greeting.api.GreetingService;
import com.example.greeting.impl.GreetingCommand.Hello;
import com.example.greeting.impl.GreetingCommand.UseGreetingMessage;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.broker.TopicProducer;
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
 * Implementation of the GreetingService.
 */
public class GreetingServiceImpl implements GreetingService {

    private final PersistentEntityRegistry persistentEntityRegistry;
    private final CassandraSession db;

    @Inject
    public GreetingServiceImpl(PersistentEntityRegistry persistentEntityRegistry, ReadSide readSide, CassandraSession db) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        this.db = db;

        persistentEntityRegistry.register(GreetingEntity.class);
        readSide.register(GreetingEventProcessor.class);
    }

    @Override
    public ServiceCall<NotUsed, String> hello(String id) {
        return request -> {
            // Look up the hello world entity for the given ID.
            PersistentEntityRef<GreetingCommand> ref = persistentEntityRegistry.refFor(GreetingEntity.class, id);
            // Ask the entity the Hello command.
            return ref.ask(new Hello(id));
        };
    }

    @Override
    public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
        return request -> {
            // Look up the hello world entity for the given ID.
            PersistentEntityRef<GreetingCommand> ref = persistentEntityRegistry.refFor(GreetingEntity.class, id);
            // Tell the entity to use the greeting message specified.
            return ref.ask(new UseGreetingMessage(request.message));
        };

    }

    @Override
    public ServiceCall<NotUsed, PSequence<String>> getGreetings(String id) {
        return req -> {
            CompletionStage<PSequence<String>> result = db.selectAll("SELECT * FROM greeting WHERE id = ?", id)
                    .thenApply(rows -> {
                        List<String> followers = rows.stream().map(row -> row.getString("message")).collect(Collectors.toList());
                        return TreePVector.from(followers);
                    });
            return result;
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<String>> getAllGreetings() {
        return req -> {
            CompletionStage<PSequence<String>> result = db.selectAll("SELECT * FROM greeting")
                    .thenApply(rows -> {
                        List<String> followers = rows.stream().map(row -> row.getString("id") + "-" + row.getString("message")).collect(Collectors.toList());
                        return TreePVector.from(followers);
                    });
            return result;
        };
    }

    @Override
    public Topic<com.example.greeting.api.GreetingEvent> helloEvents() {
        // We want to publish all the shards of the hello event
        return TopicProducer.taggedStreamWithOffset(GreetingEvent.TAG.allTags(), (tag, offset) ->

                // Load the event stream for the passed in shard tag
                persistentEntityRegistry.eventStream(tag, offset).map(eventAndOffset -> {

                    // Now we want to convert from the persisted event to the published event.
                    // Although these two events are currently identical, in future they may
                    // change and need to evolve separately, by separating them now we save
                    // a lot of potential trouble in future.
                    com.example.greeting.api.GreetingEvent eventToPublish;

                    // when GreetingEvent.GreetingMessageChanged is internally emitted from
                    // the impl, publish GreetingEvent.GreetingMessageChanged from the API version
                    if (eventAndOffset.first() instanceof GreetingEvent.GreetingMessageChanged) {
                        GreetingEvent.GreetingMessageChanged messageChanged = (GreetingEvent.GreetingMessageChanged) eventAndOffset.first();
                        eventToPublish = new com.example.greeting.api.GreetingEvent.GreetingMessageChanged(
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
