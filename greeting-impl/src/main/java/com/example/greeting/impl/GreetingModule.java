/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greeting.impl;

import com.example.greeting.api.GreetingService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * The module that binds the GreetingService so that it can be served.
 */
public class GreetingModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindService(GreetingService.class, GreetingServiceImpl.class);
    }
}
