package com.example.greeting.impl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.example.greeting.impl.GreetingCommand.Greeting;
import com.example.greeting.impl.GreetingCommand.UseGreetingMessage;
import com.example.greeting.impl.GreetingEvent.GreetingMessageChanged;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class GreetingEntityTest {

    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("GreetingEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testHelloEntity() {
        PersistentEntityTestDriver<GreetingCommand, GreetingEvent, GreetingState> driver =
                new PersistentEntityTestDriver<>(system, new GreetingEntity(), "world-1");

        Outcome<GreetingEvent, GreetingState> outcome1 = driver.run(new Greeting("Alice"));
        assertEquals("Greeting, Alice!", outcome1.getReplies().get(0));
        assertEquals(Collections.emptyList(), outcome1.issues());

        Outcome<GreetingEvent, GreetingState> outcome2 = driver.run(new UseGreetingMessage("abc", "Hi"),
                new GreetingCommand.Greeting("Bob"));
        assertEquals(1, outcome2.events().size());
        assertEquals(new GreetingMessageChanged("abc", "Hi"), outcome2.events().get(0));
        assertEquals("Hi", outcome2.state().message);
        assertEquals(Done.getInstance(), outcome2.getReplies().get(0));
        assertEquals("Hi, Bob!", outcome2.getReplies().get(1));
        assertEquals(2, outcome2.getReplies().size());
        assertEquals(Collections.emptyList(), outcome2.issues());
    }

}
