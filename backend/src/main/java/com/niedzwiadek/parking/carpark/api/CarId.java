package com.niedzwiadek.parking.carpark.api;

import java.util.UUID;

public record CarId(UUID value) {

    public static CarId random() {
        return new CarId(UUID.randomUUID());
    }

    public static CarId from(UUID id) {
        return new CarId(id);
    }

    public static CarId fromString(final String string) {
        return CarId.from(UUID.fromString(string));
    }

    public static CarId ofNullable(final String carId) {
        return carId != null ? CarId.fromString(carId) : null;
    }

    public static CarId ofNullable(final UUID carId) {
        return carId != null ? CarId.from(carId) : null;
    }

    public String serialize() {
        return value.toString();
    }
}
