package com.niedzwiadek.parking.carpark.api;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(CarId carId) {
        super("car not found for id: " + carId);
    }
}
