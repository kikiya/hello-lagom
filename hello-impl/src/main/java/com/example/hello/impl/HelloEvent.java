/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.hello.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

/**
 * This interface defines all the events that the HelloEntity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface HelloEvent extends AggregateEvent<HelloEvent>, Jsonable {

//    AggregateEventShards<HelloEvent> TAG = AggregateEventTag.sharded(HelloEvent.class, 4);

    public static final AggregateEventTag<HelloEvent> TAG =
            AggregateEventTag.of(HelloEvent.class);

    public static final AggregateEventShards<HelloEvent> SHARD_TAG = AggregateEventTag.sharded(HelloEvent.class, 4);

    /**
     * An event that represents a change in greeting message.
     */
    @SuppressWarnings("serial")
    @Value
    @JsonDeserialize
    final class GreetingMessageChanged implements HelloEvent {
        public final String id;
        public final String message;

        @JsonCreator
        public GreetingMessageChanged(String id, String message) {
            this.id = Preconditions.checkNotNull(id, "id");
            this.message = Preconditions.checkNotNull(message, "message");
        }


    }

    @Override
    default AggregateEventTag<HelloEvent> aggregateTag() {
        return TAG;
    }

//    @Override
//    default AggregateEventTagger<HelloEvent> aggregateTag() {
//        return TAG;
//    }
}
