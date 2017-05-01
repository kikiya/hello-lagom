package com.example.greeting.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.Value;

@Value
@JsonDeserialize
public final class GreetingMessage {

    public final String id;
    public final String message;

    @JsonCreator
    public GreetingMessage(String id, String message) {
        this.id = Preconditions.checkNotNull(id, "id");
        this.message = Preconditions.checkNotNull(message, "message");
    }
}
