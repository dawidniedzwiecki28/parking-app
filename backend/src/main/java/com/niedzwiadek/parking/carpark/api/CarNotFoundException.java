package com.niedzwiadek.parking.carpark.api;

public class CarNotFoundException extends RuntimeException {
  public CarNotFoundException(final CarId carId) {
    super("car not found for id: " + carId);
  }
}
