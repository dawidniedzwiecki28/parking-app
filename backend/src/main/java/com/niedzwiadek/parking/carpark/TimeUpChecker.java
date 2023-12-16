package com.niedzwiadek.parking.carpark;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUpChecker {
    public static boolean timeUpChecker(final LocalDateTime date) {
        if (date == null) {
            return false;
        }

        final var duration = Duration.between(LocalDateTime.now(), date);

        return (duration.isNegative());
    }
}
