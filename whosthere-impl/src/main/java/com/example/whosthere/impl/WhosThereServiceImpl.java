package com.example.whosthere.impl;

import akka.NotUsed;
import com.example.whosthere.api.Guest;
import com.example.whosthere.api.WhosThereService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.pcollections.PSequence;

import javax.inject.Inject;

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
