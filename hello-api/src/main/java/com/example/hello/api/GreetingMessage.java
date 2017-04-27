package com.example.hello.api;

import javax.annotation.Nullable;
import lombok.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

@Value
@JsonDeserialize
public final class GreetingMessage {

  public final String id;
  public final String message;

  @JsonCreator
  public GreetingMessage(String id, String message) {
//    this.id = Preconditions.checkNotNull(id, "id");
    this.id = id;
    this.message = Preconditions.checkNotNull(message, "message");
  }
}
