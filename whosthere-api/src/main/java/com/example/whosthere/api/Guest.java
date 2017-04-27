package com.example.whosthere.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import lombok.Value;

/**
 * Created by kiki on 4/26/17.
 */
@Value
@JsonDeserialize
public class Guest {

    public final String message;
    public final String guest;

    @JsonCreator
    public Guest(String message, String guest) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.guest = Preconditions.checkNotNull(guest, "guest");
    }

}
