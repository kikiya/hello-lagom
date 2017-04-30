/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.hellostream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.example.greeting.api.GreetingService;
import com.example.hellostream.api.HelloStreamService;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the HelloStreamService.
 */
public class HelloStreamServiceImpl implements HelloStreamService {

  private final GreetingService greetingService;

  @Inject
  public HelloStreamServiceImpl(GreetingService greetingService) {
    this.greetingService = greetingService;
  }

  @Override
  public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream() {
    return hellos -> completedFuture(
        hellos.mapAsync(8, name -> greetingService.hello(name).invoke()));
  }
}
