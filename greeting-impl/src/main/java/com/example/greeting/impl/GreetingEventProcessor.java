package com.example.greeting.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

public class GreetingEventProcessor extends ReadSideProcessor<GreetingEvent> {

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement writeGreetings = null; // initialized in prepare


    @Inject
    public GreetingEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    private void setWriteGreetings(PreparedStatement writeGreetings) {
        this.writeGreetings = writeGreetings;
    }

    @Override
    public ReadSideHandler<GreetingEvent> buildHandler() {
        return readSide.<GreetingEvent>builder("hello_offset")
                .setGlobalPrepare(this::prepareCreateTables)
                .setPrepare((ignored) -> prepareWriteGreetings())
                .setEventHandler(GreetingEvent.GreetingMessageChanged.class, this::processGreetingMessageChanged)
                .build();
    }

    @Override
    public PSequence<AggregateEventTag<GreetingEvent>> aggregateTags() {
        return GreetingEvent.TAG.allTags();
    }

    private CompletionStage<Done> prepareCreateTables() {
        // @formatter:off
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS greeting ("
                        + "id text, message text, "
                        + "PRIMARY KEY (id, message))");
        // @formatter:on
    }

    private CompletionStage<Done> prepareWriteGreetings() {
        return session.prepare("INSERT INTO greeting (id, message) VALUES (?, ?)").thenApply(ps -> {
            setWriteGreetings(ps);
            return Done.getInstance();
        });
    }

    private CompletionStage<List<BoundStatement>> processGreetingMessageChanged(GreetingEvent.GreetingMessageChanged event) {
        System.out.println("********************** EventProcessor like: "+event);
        BoundStatement bindWriteGreetings = writeGreetings.bind();
        bindWriteGreetings.setString("id", event.name);
        bindWriteGreetings.setString("message", event.message);
        return completedStatement(bindWriteGreetings);
    }
}