package com.amano.cloud.fakedoor;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Door {
    String INPUT = "vehicle-in-target";
    String OUTPUT = "vehicle-in-source";

    @Input("vehicle-in-target")
    SubscribableChannel vehicleSink();

    @Output("vehicle-in-source")
    MessageChannel vehicleSource();
}
