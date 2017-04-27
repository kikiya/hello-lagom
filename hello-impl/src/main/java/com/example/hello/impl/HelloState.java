/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.hello.impl;

import javax.annotation.Nullable;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.CompressedJsonable;

/**
 * The state for the {@link HelloEntity} entity.
 */
@SuppressWarnings("serial")
@Value
@JsonDeserialize
public final class HelloState implements CompressedJsonable {

  public final String id;
  public final String message;
  public final String timestamp;

  @JsonCreator
  public HelloState(String id, String message, String timestamp) {
    this.id = Preconditions.checkNotNull(id, "id");
    this.message = Preconditions.checkNotNull(message, "message");
    this.timestamp = Preconditions.checkNotNull(timestamp, "timestamp");
  }
}
