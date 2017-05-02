/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package com.example.greeting.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.broker.Topic;
import com.lightbend.lagom.javadsl.api.broker.kafka.KafkaProperties;
import org.pcollections.PSequence;

import static com.lightbend.lagom.javadsl.api.Service.*;

/**
 * The hello service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the GreetingService.
 */
public interface GreetingService extends Service {

    /**
     * Example: curl http://localhost:9000/api/hello/Alice
     */
    ServiceCall<NotUsed, String> hello(String id);

    /**
     * Example: curl -H "Content-Type: application/json" -X POST -d '{"message":
     * "Hi"}' http://localhost:9000/api/hello/Alice
     */
    ServiceCall<GreetingMessage, Done> useGreeting(String id);

    /**
     * Example: curl http://localhost:9000/api/hello/Alice/greetings
     */
    ServiceCall<NotUsed, PSequence<String>> getGreetings(String id);

    /**
     * Example: curl http://localhost:9000/api/all/stuff
     */
    ServiceCall<NotUsed, PSequence<String>> getAllGreetings();

    /**
     * This gets published to Kafka.
     */
    Topic<GreetingEvent> helloEvents();

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("greeting").withCalls(
                pathCall("/api/hello/:id",  this::hello),
                pathCall("/api/hello/:id", this::useGreeting),
                pathCall("/api/hello/:id/greetings", this::getGreetings),
                pathCall("/api/hello/all/stuff", this::getAllGreetings)
        ).publishing(
                topic("hello-events", this::helloEvents)
                        // Kafka partitions messages, messages within the same partition will
                        // be delivered in order, to ensure that all messages for the same user
                        // go to the same partition (and hence are delivered in order with respect
                        // to that user), we configure a partition key strategy that extracts the
                        // name as the partition key.
                        .withProperty(KafkaProperties.partitionKeyStrategy(), GreetingEvent::getName)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
