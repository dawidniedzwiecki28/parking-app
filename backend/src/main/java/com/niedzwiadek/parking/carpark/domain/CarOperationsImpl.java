package com.niedzwiadek.parking.carpark.domain;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.carpark.api.CarId;
import com.niedzwiadek.parking.carpark.infrastructure.BlacklistCarEntity;
import com.niedzwiadek.parking.carpark.infrastructure.BlacklistCarRepository;
import com.niedzwiadek.parking.carpark.infrastructure.CarEntity;
import com.niedzwiadek.parking.carpark.infrastructure.CarRepository;
import com.niedzwiadek.parking.carpark.api.CarOperations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
class CarOperationsImpl implements CarOperations {

    private static final Logger log = LoggerFactory.getLogger(CarOperationsImpl.class);
    private final CarRepository repository;
    private final BlacklistCarRepository blackListCarRepository;

    @Override
    @Transactional
    public void update(CarUpdate update) {
        log.info("Updating car: {}", update);
        final var entity = repository.findById(update.getCarId().value())
                .orElseThrow();
        update.getRegistrationNumber().ifPresent(entity::setRegistrationNumber);
        update.getArrivalDate().ifPresent(entity::setArrivalDate);
        update.getDepartureDate().ifPresent(entity::setDepartureDate);
        update.getCountry().ifPresent(entity::setCountry);
        update.getOnParking().ifPresent(entity::setOnParking);
        update.getPaid().ifPresent(entity::setPaid);
        log.info("Updated car: {}", entity);
        repository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void create(CarCreate sourceCar) {
        repository.deleteByRegistrationNumberIgnoreCase(sourceCar.getRegistrationNumber());
        blackListCarRepository.deleteByRegistrationNumberIgnoreCase(sourceCar.getRegistrationNumber());
        final var entity = CarEntity.builder()
                .accountId(sourceCar.getAccountId().value())
                .carId(UUID.randomUUID())
                .registrationNumber(sourceCar.getRegistrationNumber())
                .onParking(sourceCar.isOnParking())
                .arrivalDate(sourceCar.getArrivalDate())
                .departureDate(sourceCar.getDepartureDate())
                .country(sourceCar.getCountry())
                .paid(sourceCar.getPaid())
                .build();
        repository.saveAndFlush(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarData> listCarsOnParking(@NonNull AccountId accountId) {
        final var carsOnParking = repository.findAllByAccountIdAndOnParkingTrue(accountId.value());
        log.info("Cars on parking: {}", carsOnParking);
        return carsOnParking.stream()
                .map(entity -> CarData.builder()
                        .carId(CarId.from(entity.getCarId()))
                        .registrationNumber(entity.getRegistrationNumber())
                        .accountId(AccountId.from(entity.getAccountId()))
                        .arrivalDate(entity.getArrivalDate())
                        .departureDate(entity.getDepartureDate())
                        .country(entity.getCountry())
                        .paid(entity.isPaid())
                        .onParking(entity.isOnParking())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CarData find(@NonNull String registrationNumber) {
        final var entity = repository.findByRegistrationNumberIgnoreCase(registrationNumber)
                .orElseThrow();
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

    @Override
    @Transactional
    public void addToBlackList(@NonNull CarId carId) {
        final var carEntity = repository.findById(carId.value()).orElseThrow();
        final var now = LocalDateTime.now();
        blackListCarRepository.saveAndFlush(BlacklistCarEntity.builder()
                .carId(carId.value())
                .ranAwayAt(now)
                .registrationNumber(carEntity.getRegistrationNumber())
                .build());
        update(CarUpdate.builder()
                .carId(carId)
                .departureDate(Optional.of(now))
                .onParking(Optional.of(false))
                .paid(Optional.of(false))
                .registrationNumber(Optional.empty())
                .arrivalDate(Optional.empty())
                .country(Optional.empty())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkIfBlacklisted(@NonNull String registrationNumber) {
        return blackListCarRepository.findByRegistrationNumberIgnoreCase(registrationNumber).isPresent();
    }
}
