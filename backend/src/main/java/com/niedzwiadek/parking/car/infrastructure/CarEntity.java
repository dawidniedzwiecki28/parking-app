package com.niedzwiadek.parking.car.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cars")
public class CarEntity {
  @Id
  private UUID carId;
  @NonNull
  @Column(unique = true)
  private String registrationNumber;
  @NonNull
  private UUID accountId;
  private String country;
  private boolean paid;
  @NonNull
  private LocalDateTime arrivalDate;
  private LocalDateTime departureDate;
  private boolean onParking;
}
