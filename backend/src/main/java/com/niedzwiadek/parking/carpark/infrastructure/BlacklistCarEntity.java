package com.niedzwiadek.parking.carpark.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "black_list_cars")
public class BlacklistCarEntity {
    @Id
    private UUID carId;
    private String registrationNumber;
    private LocalDateTime ranAwayAt;
}