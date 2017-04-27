//package com.example.hello.impl;
//
//import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
//import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
//
//public class HelloEventTag {
//
//    public static final AggregateEventTag<HelloEvent> TAG =
//            AggregateEventTag.of(HelloEvent.class);
//
//    public static final AggregateEventShards<HelloEvent> INSTANCE = AggregateEventTag.sharded(HelloEvent.class, 4);
//}
