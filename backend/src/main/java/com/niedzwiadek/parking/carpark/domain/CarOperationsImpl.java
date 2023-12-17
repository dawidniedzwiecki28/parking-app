package com.niedzwiadek.parking.carpark.domain;

import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.carpark.api.CarId;
import com.niedzwiadek.parking.carpark.api.CarNotFoundException;
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
    public void update(@NonNull final CarUpdate update) {
        log.info("Updating car: {}", update);
        final var entity = repository.findById(update.getCarId().value())
                .orElseThrow(() -> new CarNotFoundException(update.getCarId()));
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
    public void create(@NonNull final CarCreate sourceCar) {
        repository.deleteByRegistrationNumberIgnoreCase(sourceCar.getRegistrationNumber());
        blackListCarRepository.deleteByRegistrationNumberIgnoreCase(sourceCar.getRegistrationNumber());
        final var entity = CarEntity.builder()
                .accountId(sourceCar.getAccountId().value())
                .carId(UUID.randomUUID())
                .registrationNumber(sourceCar.getRegistrationNumber())
                .arrivalDate(sourceCar.getArrivalDate() != null ? sourceCar.getArrivalDate() : LocalDateTime.now())
                .departureDate(sourceCar.getDepartureDate())
                .country(sourceCar.getCountry())
                .paid(sourceCar.getPaid())
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
    public Optional<CarData> find(@NonNull final String registrationNumber) {
        final var entity = repository.findByRegistrationNumberIgnoreCase(registrationNumber);
        return entity.map(this::fromEntity);
    }

    @Override
    @Transactional
    public void addToBlackList(@NonNull final CarId carId) {
        final var carEntity = repository.findById(carId.value())
                .orElseThrow(() -> new CarNotFoundException(carId));
        final var now = LocalDateTime.now();
        blackListCarRepository.saveAndFlush(BlacklistCarEntity.builder()
                .carId(carId.value())
                .ranAwayAt(now)
                .registrationNumber(carEntity.getRegistrationNumber())
                .build());
        updateBlacklisted(carId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkIfBlacklisted(@NonNull final String registrationNumber) {
        return blackListCarRepository.findByRegistrationNumberIgnoreCase(registrationNumber).isPresent();
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


    private void updateBlacklisted(final CarId carId) {
        update(CarUpdate.builder()
                .carId(carId)
                .departureDate(Optional.of(LocalDateTime.now()))
                .onParking(Optional.of(false))
                .paid(Optional.of(false))
                .registrationNumber(Optional.empty())
                .arrivalDate(Optional.empty())
                .country(Optional.empty())
                .build());
    }
}
