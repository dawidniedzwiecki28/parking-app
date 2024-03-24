package com.niedzwiadek.parking.car.api;

import com.niedzwiadek.parking.account.api.AccountId;
import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CarOperations {

  void update(@NonNull CarUpdate update);

  void create(@NonNull CarCreate sourceCar);

  List<CarData> listCarsOnParking(@NonNull AccountId accountId, String term);

  CarData get(@NonNull String registrationNumber);

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
