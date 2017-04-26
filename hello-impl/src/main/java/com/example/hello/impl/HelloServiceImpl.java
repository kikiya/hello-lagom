/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.hello.impl;

import akka.Done;
import akka.NotUsed;
import com.example.hello.api.GreetingMessage;
import com.example.hello.api.HelloService;
import com.example.hello.impl.HelloCommand.Hello;
import com.example.hello.impl.HelloCommand.UseGreetingMessage;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
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
            return ref.ask(new Hello(id, Optional.empty()));
            //return CompletableFuture.completedFuture("Hello someone");
        };
    }

    @Override
    public ServiceCall<GreetingMessage, Done> useGreeting(String id) {
        return request -> {
            // Look up the hello world entity for the given ID.
            PersistentEntityRef<HelloCommand> ref = persistentEntityRegistry.refFor(HelloEntity.class, id);
            // Tell the entity to use the greeting message specified.
            return ref.ask(new UseGreetingMessage(id, request.message));
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
}
