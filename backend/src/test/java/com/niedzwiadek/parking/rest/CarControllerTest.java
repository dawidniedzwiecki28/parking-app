package com.niedzwiadek.parking.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedzwiadek.parking.account.api.AccountId;
import com.niedzwiadek.parking.account.api.AccountOperations;
import com.niedzwiadek.parking.car.api.CarId;
import com.niedzwiadek.parking.car.api.CarNotFoundException;
import com.niedzwiadek.parking.car.api.CarOperations;
import com.niedzwiadek.parking.rest.carpark.CarController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.niedzwiadek.parking.rest.carpark.CarController.CARS_SEARCH_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CAR_FIND_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CAR_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CAR_UPDATE_PATH;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(value = CarControllerTest.email)
public class CarControllerTest {

  private static final AccountId accountId = AccountId.fromString("17c4d0ea-72b7-4b2a-9bde-918750724377");
  private static final CarId carId = CarId.fromString("03b23a76-7450-4528-abf2-2fc551ec7c0b");
  static final String email = "test@example.com";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private AccountOperations accountOperations;
  @MockBean
  private CarOperations carOperations;

  @Test
  void shouldSearchCars() throws Exception {
    // given
    stubCurrentUser();
    final var carId2 = CarId.fromString("52a5c1f3-bec5-4f4e-bdfa-87806ad57fab");
    final var carId3 = CarId.fromString("1485a568-aaf0-4eb8-a9c0-81fd513993ff");
    final var now = LocalDateTime.parse("2024-03-24T00:38:48.125097");
    given(carOperations.listCarsOnParking(accountId, "term"))
        .willReturn(List.of(
            CarOperations.CarData.builder()
                .carId(carId)
                .registrationNumber("registrationNumber")
                .country("Poland")
                .accountId(accountId)
                .arrivalDate(now)
                .departureDate(now.plusHours(5))
                .onParking(true)
                .build(),
            CarOperations.CarData.builder()
                .carId(carId2)
                .registrationNumber("registrationNumber2")
                .country("Ukraine")
                .accountId(accountId)
                .arrivalDate(now.minusHours(2))
                .onParking(false)
                .build(),
            CarOperations.CarData.builder()
                .carId(carId3)
                .registrationNumber("registrationNumber3")
                .accountId(accountId)
                .arrivalDate(now)
                .departureDate(now.plusHours(5))
                .onParking(true)
                .build()
        ));


    // when
    mockMvc.perform(get(CARS_SEARCH_PATH, "term"))

        // then
        .andExpect(status().isOk())
        .andExpect(content().json("""
                [
                  {
                    "accountId": "17c4d0ea-72b7-4b2a-9bde-918750724377",
                    "carId": "03b23a76-7450-4528-abf2-2fc551ec7c0b",
                    "registrationNumber": "registrationNumber",
                    "country": "Poland",
                    "arrivalDate": "2024-03-24T00:38:48.125097",
                    "departureDate": "2024-03-24T05:38:48.125097",
                    "paid": false,
                    "onParking": true
                  },
                  {
                    "accountId": "17c4d0ea-72b7-4b2a-9bde-918750724377",
                    "carId": "52a5c1f3-bec5-4f4e-bdfa-87806ad57fab",
                    "registrationNumber": "registrationNumber2",
                    "country": "Ukraine",
                    "arrivalDate": "2024-03-23T22:38:48.125097",
                    "departureDate": null,
                    "paid": false,
                    "onParking": false
                  },
                  {
                    "accountId": "17c4d0ea-72b7-4b2a-9bde-918750724377",
                    "carId": "1485a568-aaf0-4eb8-a9c0-81fd513993ff",
                    "registrationNumber": "registrationNumber3",
                    "country": null,
                    "arrivalDate": "2024-03-24T00:38:48.125097",
                    "departureDate": "2024-03-24T05:38:48.125097",
                    "paid": false,
                    "onParking": true
                  }
                ]
            """
        ));
  }

  @Test
  void shouldFindCar() throws Exception {
    // given
    final var now = LocalDateTime.parse("2024-03-24T00:38:48.125097");
    given(carOperations.get("registrationNumber"))
        .willReturn(CarOperations.CarData.builder()
            .carId(carId)
            .registrationNumber("registrationNumber")
            .country("Poland")
            .accountId(accountId)
            .arrivalDate(now)
            .departureDate(now.plusHours(5))
            .onParking(true)
            .build());

    // when
    mockMvc.perform(get(CAR_FIND_PATH, "registrationNumber"))

        // then
        .andExpect(status().isOk())
        .andExpect(content().json("""
                  {
                    "accountId": "17c4d0ea-72b7-4b2a-9bde-918750724377",
                    "carId": "03b23a76-7450-4528-abf2-2fc551ec7c0b",
                    "registrationNumber": "registrationNumber",
                    "country": "Poland",
                    "arrivalDate": "2024-03-24T00:38:48.125097",
                    "departureDate": "2024-03-24T05:38:48.125097",
                    "paid": false,
                    "onParking": true
                  }
            """
        ));
  }

  @Test
  void shouldReturnNotFoundWhenGettingCar() throws Exception {
    // given
    final var registrationNumber = "registrationNumber";
    given(carOperations.get("registrationNumber"))
        .willThrow(new CarNotFoundException(registrationNumber));

    // when
    mockMvc.perform(get(CAR_FIND_PATH, registrationNumber))

        // then
        .andExpect(status().isNotFound())
        .andExpect(content().string("Car not found for registration number: " + registrationNumber));
  }

  @Test
  void shouldCreateCar() throws Exception {
    // given
    stubCurrentUser();
    final var now = LocalDateTime.now();
    final var body = CarController.CarCreateDto.builder()
        .registrationNumber("registrationNumber")
        .arrivalDate(now)
        .departureDate(now.plusHours(15))
        .country("Poland")
        .paid(false)
        .build();
    final var expectedCarCreate = CarOperations.CarCreate.builder()
        .accountId(accountId)
        .registrationNumber("registrationNumber")
        .arrivalDate(now)
        .departureDate(now.plusHours(15))
        .country("Poland")
        .paid(false)
        .build();

    // when
    mockMvc.perform(post(CAR_PATH)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body)))

        // then
        .andExpect(status().isOk());

    // and
    then(carOperations).should().create(expectedCarCreate);
  }

  @Test
  void shouldUpdateCar() throws Exception {
    // given
    stubCurrentUser();
    final var now = LocalDateTime.now();
    final var body = CarController.CarUpdateDto.builder()
        .departureDate(now.plusHours(15))
        .onParking(true)
        .build();
    final var expectedCarUpdate = CarOperations.CarUpdate.builder()
        .carId(carId)
        .registrationNumber(Optional.empty())
        .arrivalDate(Optional.empty())
        .departureDate(Optional.of(now.plusHours(15)))
        .paid(Optional.empty())
        .country(Optional.empty())
        .onParking(Optional.of(true))
        .build();

    // when
    mockMvc.perform(patch(CAR_UPDATE_PATH, carId.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body)))

        // then
        .andExpect(status().isOk());

    // and
    then(carOperations).should().update(expectedCarUpdate);
  }

  @Test
  void shouldThrowNotFoundWhenUpdatingCar() throws Exception {
    // given
    stubCurrentUser();
    final var now = LocalDateTime.now();
    final var body = CarController.CarUpdateDto.builder()
        .departureDate(now.plusHours(15))
        .onParking(true)
        .build();
    final var expectedCarUpdate = CarOperations.CarUpdate.builder()
        .carId(carId)
        .registrationNumber(Optional.empty())
        .arrivalDate(Optional.empty())
        .departureDate(Optional.of(now.plusHours(15)))
        .paid(Optional.empty())
        .country(Optional.empty())
        .onParking(Optional.of(true))
        .build();
    willThrow(new CarNotFoundException(carId))
        .given(carOperations).update(expectedCarUpdate);

    // when
    mockMvc.perform(patch(CAR_UPDATE_PATH, carId.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(body)))

        // then
        .andExpect(status().isNotFound())
        .andExpect(content().string("Car not found for id: " + carId));
  }

  private void stubCurrentUser() {
    given(accountOperations.findAccountIdByEmail(email)).willReturn(accountId);
  }
}
