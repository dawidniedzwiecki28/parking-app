package com.niedzwiadek.parking.carpark.api;

import com.niedzwiadek.parking.account.api.AccountId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarOperations {

    @Transactional
    void update(CarUpdate update);

    @Transactional
    void create(CarCreate sourceCar);

    @Transactional(readOnly = true)
    List<CarData> listCarsOnParking(@NonNull AccountId accountId, String term);

    @Transactional(readOnly = true)
    Optional<CarData> find(@NonNull String registrationNumber);

    @Transactional
    void addToBlackList(@NonNull CarId carId);

    @Transactional(readOnly = true)
    boolean checkIfBlacklisted(@NonNull String registrationNumber);

    @Transactional
    void deleteCarsFor(@NonNull AccountId accountId);

    @Value
    @Builder
    class CarUpdate {
        @NonNull
        CarId carId;
        Optional<String> registrationNumber;
        Optional<String> country;
        Optional<LocalDateTime> departureDate;
        Optional<LocalDateTime> arrivalDate;
        Optional<Boolean> paid;
        Optional<Boolean> onParking;
    }

    @Value
    @Builder
    class CarCreate {
        @NonNull
        AccountId accountId;
        @NonNull
        String registrationNumber;
        String country;
        LocalDateTime departureDate;
        LocalDateTime arrivalDate;
        Boolean paid;
    }

    @Value
    @Builder
    class CarData {
        @NonNull
        AccountId accountId;
        @NonNull
        CarId carId;
        @NonNull
        String registrationNumber;
        String country;
        LocalDateTime departureDate;
        @NonNull
        LocalDateTime arrivalDate;
        boolean paid;
        boolean onParking;
    }
}
