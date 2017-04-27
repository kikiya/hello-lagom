package com.example.whosthere.impl;

import akka.Done;
import akka.NotUsed;
import com.example.hello.api.HelloService;
import com.example.whosthere.api.Guest;
import com.example.whosthere.api.WhosThereService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Created by kiki on 4/26/17.
 */
public class WhosThereServiceImpl implements WhosThereService {

    private final WhosThereRepository repository;

    @Inject
    public WhosThereServiceImpl(WhosThereRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceCall<NotUsed, PSequence<Guest>> whosthere() {
        return req -> repository.getGuests();
    }
}
