/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greeting.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

/**
 * This interface defines all the events that the GreetingEntity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface GreetingEvent extends AggregateEvent<GreetingEvent>, Jsonable {

//    AggregateEventShards<GreetingEvent> TAG = AggregateEventTag.sharded(GreetingEvent.class, 4);

//    @Override
//    default AggregateEventTagger<GreetingEvent> aggregateTag() {
//        return TAG;
//    }

    AggregateEventTag<GreetingEvent> TAG = AggregateEventTag.of(GreetingEvent.class);

    AggregateEventShards<GreetingEvent> SHARD_TAG = AggregateEventTag.sharded(GreetingEvent.class, 4);

    @Override
    default AggregateEventTag<GreetingEvent> aggregateTag() {
        return TAG;
    }

    /**
     * An event that represents a change in greeting message.
     */
    @SuppressWarnings("serial")
    @Value
    @JsonDeserialize
    final class GreetingMessageChanged implements GreetingEvent {
        public final String id;
        public final String message;

        @JsonCreator
        public GreetingMessageChanged(String id, String message) {
            this.id = Preconditions.checkNotNull(id, "id");
            this.message = Preconditions.checkNotNull(message, "message");
        }
    }
}
