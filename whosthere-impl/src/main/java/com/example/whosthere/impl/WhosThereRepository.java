package com.example.whosthere.impl;

import akka.Done;
import com.example.whosthere.api.Guest;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Created by kiki on 4/26/17.
 */
@Singleton
public class WhosThereRepository {
    private final CassandraSession uninitialisedSession;

    // Will return the session when the Cassandra tables have been successfully created
    private volatile CompletableFuture<CassandraSession> initialisedSession;

    @Inject
    public WhosThereRepository(CassandraSession uninitialisedSession) {
        this.uninitialisedSession = uninitialisedSession;
        // Eagerly create the session
        session();
    }

    private CompletionStage<CassandraSession> session() {
        // If there's no initialised session, or if the initialised session future completed
        // with an exception, then reinitialise the session and attempt to create the tables
        if (initialisedSession == null || initialisedSession.isCompletedExceptionally()) {
            initialisedSession = uninitialisedSession.executeCreateTable(
                    "CREATE TABLE IF NOT EXISTS lagom_guests (guest text PRIMARY KEY, message text)"
            ).thenApply(done -> uninitialisedSession).toCompletableFuture();
        }
        return initialisedSession;
    }

    public CompletionStage<Done> addGuest(String guest, String message) {
        return session().thenCompose(session ->
                session.executeWrite("INSERT INTO lagom_guests (guest, message) VALUES (?, ?)", guest, message)
        );
    }

    public CompletionStage<PSequence<Guest>> getGuests() {
        return session().thenCompose(session ->
                session.selectAll("SELECT * FROM lagom_guests "))
                .thenApply(rows -> {
                    List<Guest> guest = rows.stream().map(row ->
                            new Guest(
                                    row.getString("message"),
                                    row.getString("guest")))
                            .collect(Collectors.toList());
                    return TreePVector.from(guest);
                });
    }
}
