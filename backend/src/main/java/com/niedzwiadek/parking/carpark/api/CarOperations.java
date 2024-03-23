package com.niedzwiadek.parking.carpark.api;

import com.niedzwiadek.parking.account.api.AccountId;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarOperations {

  @Transactional
  void update(@NonNull CarUpdate update);

  @Transactional
  void create(@NonNull CarCreate sourceCar);

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


  @Builder
  record CarUpdate(
      @NonNull CarId carId,
      @NonNull Optional<String> registrationNumber,
      @NonNull Optional<String> country,
      @NonNull Optional<LocalDateTime> departureDate,
      @NonNull Optional<LocalDateTime> arrivalDate,
      @NonNull Optional<Boolean> paid,
      @NonNull Optional<Boolean> onParking) {
  }

  @Builder
  record CarCreate(
      @NonNull
      AccountId accountId,
      @NonNull
      String registrationNumber,
      String country,
      LocalDateTime departureDate,
      LocalDateTime arrivalDate,
      Boolean paid) {
  }

  @Builder
  record CarData(
      @NonNull
      AccountId accountId,
      @NonNull
      CarId carId,
      @NonNull
      String registrationNumber,
      String country,
      LocalDateTime departureDate,
      @NonNull
      LocalDateTime arrivalDate,
      boolean paid,
      boolean onParking) {
  }
}
