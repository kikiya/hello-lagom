/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greeting.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;
import lombok.Value;

/**
 * The state for the {@link GreetingEntity} entity.
 */
@SuppressWarnings("serial")
@Value
@JsonDeserialize
public final class GreetingState implements CompressedJsonable {

    public final String id;
    public final String message;
    public final String timestamp;

    @JsonCreator
    public GreetingState(String id, String message, String timestamp) {
        this.id = Preconditions.checkNotNull(id, "id");
        this.message = Preconditions.checkNotNull(message, "message");
        this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
    }
}
