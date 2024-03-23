package com.niedzwiadek.parking.rest.carpark;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountOperations;
import com.niedzwiadek.parking.carpark.api.CarId;
import com.niedzwiadek.parking.carpark.api.CarOperations;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/parking")
@AllArgsConstructor
public class CarController {

  private static final Logger log = LoggerFactory.getLogger(CarController.class);
  private final CarOperations carOperations;
  private final AccountOperations accountOperations;

  @GetMapping("/cars/{term}")
  List<CarDataResponseDto> searchCarsOnParking(final Principal principal, @PathVariable final String term) {
    final var accountId = accountOperations.findAccountIdByEmail(principal.getName());
    return carOperations.listCarsOnParking(accountId, term).stream()
        .map(this::fromCarData)
        .toList();
  }

  @GetMapping("/cars")
  List<CarDataResponseDto> listAllCarsOnParking(final Principal principal) {
    final var accountId = accountOperations.findAccountIdByEmail(principal.getName());
    return carOperations.listCarsOnParking(accountId, null).stream()
        .map(this::fromCarData)
        .toList();
  }

  @GetMapping("/car/{number}")
  Optional<CarDataResponseDto> find(@PathVariable @NonNull final String number) {
    final var car = carOperations.find(number);
    return car.map(this::fromCarData);
  }

  @PostMapping("/car")
  void create(final Principal principal, @RequestBody @NonNull final CarCreateDto sourceCar) {
    log.info("Received request to create car: {}", sourceCar);
    final var accountId = accountOperations.findAccountIdByEmail(principal.getName());
    carOperations.create(fromCarCreateDto(sourceCar, accountId));
  }

  @PatchMapping("/car/{carId}")
  void update(@PathVariable @NonNull final String carId,
              @RequestBody @NonNull final CarUpdateDto sourceCar) {
    log.info("Received request to update car: {}", sourceCar);
    carOperations.update(fromCarUpdateDto(sourceCar, CarId.fromString(carId)));
  }

  private CarDataResponseDto fromCarData(final CarOperations.CarData sourceCar) {
    return CarDataResponseDto.builder()
        .carId(sourceCar.carId().serialize())
        .accountId(sourceCar.accountId().serialize())
        .registrationNumber(sourceCar.registrationNumber())
        .arrivalDate(sourceCar.arrivalDate())
        .departureDate(sourceCar.departureDate())
        .country(sourceCar.country())
        .paid(sourceCar.paid())
        .onParking(sourceCar.onParking())
        .build();
  }

  private CarOperations.CarUpdate fromCarUpdateDto(final CarUpdateDto sourceCar, final CarId carId) {
    return CarOperations.CarUpdate.builder()
        .carId(carId)
        .registrationNumber(Optional.ofNullable(sourceCar.registrationNumber()))
        .arrivalDate(Optional.ofNullable(sourceCar.arrivalDate()))
        .departureDate(Optional.ofNullable(sourceCar.departureDate()))
        .country(Optional.ofNullable(sourceCar.country()))
        .onParking(Optional.ofNullable(sourceCar.onParking()))
        .paid(Optional.ofNullable(sourceCar.paid()))
        .build();
  }

  private CarOperations.CarCreate fromCarCreateDto(final CarCreateDto sourceCar, final AccountId accountId) {
    return CarOperations.CarCreate.builder()
        .accountId(accountId)
        .registrationNumber(sourceCar.registrationNumber())
        .arrivalDate(sourceCar.arrivalDate())
        .departureDate(sourceCar.departureDate())
        .country(sourceCar.country())
        .paid(sourceCar.paid())
        .build();
  }

  @Builder
  record CarCreateDto(
      @NonNull
      String registrationNumber,
      String country,
      LocalDateTime arrivalDate,
      LocalDateTime departureDate,
      boolean paid) {
  }

  @Builder
  record CarDataResponseDto(
      @NonNull
      String accountId,
      @NonNull
      String carId,
      @NonNull
      String registrationNumber,
      String country,
      @NonNull
      LocalDateTime arrivalDate,
      LocalDateTime departureDate,
      boolean paid,
      boolean onParking) {
  }

  @Builder
  record CarUpdateDto(
      @NonNull
      String registrationNumber,
      String country,
      LocalDateTime arrivalDate,
      LocalDateTime departureDate,
      Boolean paid,
      Boolean onParking) {
  }
}
