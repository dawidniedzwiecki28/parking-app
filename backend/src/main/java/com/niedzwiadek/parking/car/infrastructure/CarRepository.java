package com.niedzwiadek.parking.car.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, UUID> {
  Optional<CarEntity> findByRegistrationNumberIgnoreCase(String number);

  List<CarEntity> findAllByAccountIdAndOnParkingTrue(UUID accountId);

  void deleteByRegistrationNumberIgnoreCase(String number);

  void deleteAllByAccountId(UUID accountId);
}
