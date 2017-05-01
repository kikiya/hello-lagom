/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greeting.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Value;

/**
 * This interface defines all the commands that the GreetingEntity supports.
 * <p>
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface GreetingCommand extends Jsonable {

    /**
     * A command to switch the greeting message.
     * <p>
     * It has a reply type of {@link akka.Done}, which is sent back to the caller
     * when all the events emitted by this command are successfully persisted.
     */
    @SuppressWarnings("serial")
    @Value
    @JsonDeserialize
    final class UseGreetingMessage implements GreetingCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        public final String id;
        public final String message;

        @JsonCreator
        UseGreetingMessage(String id, String message) {
            this.id = Preconditions.checkNotNull(id, "id");
            this.message = Preconditions.checkNotNull(message, "message");
        }
    }

    /**
     * A command to say hello to someone using the current greeting message.
     * <p>
     * The reply type is String, and will contain the message to say to that
     * person.
     */
    @SuppressWarnings("serial")
    @Value
    @JsonDeserialize
    final class Greeting implements GreetingCommand, PersistentEntity.ReplyType<String> {
        public final String name;

        @JsonCreator
        Greeting(String name) {
            this.name = Preconditions.checkNotNull(name, "name");
        }
    }

}