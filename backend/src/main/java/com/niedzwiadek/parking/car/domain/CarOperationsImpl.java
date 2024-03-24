package com.niedzwiadek.parking.car.domain;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.car.api.CarId;
import com.niedzwiadek.parking.car.api.CarNotFoundException;
import com.niedzwiadek.parking.car.api.CarOperations;
import com.niedzwiadek.parking.car.infrastructure.CarEntity;
import com.niedzwiadek.parking.car.infrastructure.CarRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
class CarOperationsImpl implements CarOperations {

  private static final Logger log = LoggerFactory.getLogger(CarOperationsImpl.class);
  private final CarRepository repository;

  @Override
  @Transactional
  public void update(@NonNull final CarUpdate update) {
    log.info("Updating car: {}", update);
    final var entity = repository.findById(update.carId().value())
        .orElseThrow(() -> new CarNotFoundException(update.carId()));
    update.registrationNumber().ifPresent(entity::setRegistrationNumber);
    update.arrivalDate().ifPresent(entity::setArrivalDate);
    update.departureDate().ifPresent(entity::setDepartureDate);
    update.country().ifPresent(entity::setCountry);
    update.onParking().ifPresent(entity::setOnParking);
    update.paid().ifPresent(entity::setPaid);
    log.info("Updated car: {}", entity);
    repository.saveAndFlush(entity);
  }

  @Override
  @Transactional
  public void create(@NonNull final CarCreate sourceCar) {
    repository.deleteByRegistrationNumberIgnoreCase(sourceCar.registrationNumber());
    final var entity = CarEntity.builder()
        .accountId(sourceCar.accountId().value())
        .carId(UUID.randomUUID())
        .registrationNumber(sourceCar.registrationNumber())
        .arrivalDate(sourceCar.arrivalDate() != null ? sourceCar.arrivalDate() : LocalDateTime.now())
        .departureDate(sourceCar.departureDate())
        .country(sourceCar.country())
        .paid(sourceCar.paid())
        .onParking(true)
        .build();
    repository.saveAndFlush(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CarData> listCarsOnParking(@NonNull final AccountId accountId, final String term) {
    final var carsOnParking = repository.findAllByAccountIdAndOnParkingTrue(accountId.value());
    log.info("Cars on parking: {}", carsOnParking);
    if (term == null) {
      return carsOnParking.stream().map(this::fromEntity).toList();
    }
    return carsOnParking.stream()
        .filter(it -> it.getRegistrationNumber().contains(term))
        .map(this::fromEntity)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CarData get(@NonNull final String registrationNumber) {
    final var entity = repository.findByRegistrationNumberIgnoreCase(registrationNumber)
        .orElseThrow(() -> new CarNotFoundException(registrationNumber));
    return fromEntity(entity);
  }

  @Override
  @Transactional
  public void deleteCarsFor(@NonNull final AccountId accountId) {
    log.info("Deleting all cars for {}", accountId);
    repository.deleteAllByAccountId(accountId.value());
  }

  private CarData fromEntity(final CarEntity entity) {
    return CarData.builder()
        .carId(CarId.from(entity.getCarId()))
        .registrationNumber(entity.getRegistrationNumber())
        .accountId(AccountId.from(entity.getAccountId()))
        .arrivalDate(entity.getArrivalDate())
        .departureDate(entity.getDepartureDate())
        .country(entity.getCountry())
        .paid(entity.isPaid())
        .onParking(entity.isOnParking())
        .build();
  }
}
