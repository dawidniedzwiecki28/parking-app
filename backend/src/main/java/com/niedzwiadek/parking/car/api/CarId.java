package com.niedzwiadek.parking.car.api;

import lombok.NonNull;

import java.util.UUID;

public record CarId(@NonNull UUID value) {

  public static CarId random() {
    return new CarId(UUID.randomUUID());
  }

  public static CarId from(@NonNull final UUID id) {
    return new CarId(id);
  }

  public static CarId fromString(@NonNull final String string) {
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
