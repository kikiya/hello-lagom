package com.example.whosthere.impl;

import com.example.hello.api.HelloService;
import com.example.whosthere.api.WhosThereService;
import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Created by kiki on 4/26/17.
 */
public class WhosThereModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        // Bind the HelloStreamService service
        bindServices(serviceBinding(WhosThereService.class, WhosThereServiceImpl.class));
        // Bind the HelloService client
        bindClient(HelloService.class);
        // Bind the subscriber eagerly to ensure it starts up
        bind(WhosThereSubscriber.class).asEagerSingleton();
    }
}
