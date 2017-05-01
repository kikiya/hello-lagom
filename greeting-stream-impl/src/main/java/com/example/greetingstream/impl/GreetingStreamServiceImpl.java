/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greetingstream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.example.greetingstream.api.GreetingStreamService;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.example.greeting.api.GreetingService;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the GreetingStreamService.
 */
public class GreetingStreamServiceImpl implements GreetingStreamService {

  private final GreetingService greetingService;

  @Inject
  public GreetingStreamServiceImpl(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @Override
  public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream() {
    return hellos -> completedFuture(
        hellos.mapAsync(8, name -> greetingService.hello(name).invoke()));
  }
}
