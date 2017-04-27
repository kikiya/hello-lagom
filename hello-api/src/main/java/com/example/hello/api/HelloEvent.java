package com.example.hello.api;

/**
 * Created by kiki on 4/27/17.
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import lombok.Value;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HelloEvent.GreetingMessageChanged.class, name = "greeting-message-changed")
})
public interface HelloEvent {

        String getId();

@Value
final class GreetingMessageChanged implements HelloEvent {

public final String id;
public final String message;

@JsonCreator
public GreetingMessageChanged(String name, String message) {
        this.id = Preconditions.checkNotNull(name, "name");
        this.message = Preconditions.checkNotNull(message, "message");
        }

    @Override
    public String getId() {
        return this.id;
    }
}


}

