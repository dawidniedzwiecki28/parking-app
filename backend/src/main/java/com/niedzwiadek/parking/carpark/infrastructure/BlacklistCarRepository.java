package com.niedzwiadek.parking.carpark.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BlacklistCarRepository extends JpaRepository<BlacklistCarEntity, UUID> {
  Optional<BlacklistCarEntity> findByRegistrationNumberIgnoreCase(String number);

  void deleteByRegistrationNumberIgnoreCase(String number);
}
