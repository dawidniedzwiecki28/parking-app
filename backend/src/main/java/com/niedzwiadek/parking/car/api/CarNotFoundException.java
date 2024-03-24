package com.niedzwiadek.parking.car.api;

public class CarNotFoundException extends RuntimeException {
  public CarNotFoundException(final CarId carId) {
    super("Car not found for id: " + carId);
  }

  public CarNotFoundException(final String registrationNumber) {
    super("Car not found for registration number: " + registrationNumber);
  }
}
