package com.niedzwiadek.parking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niedzwiadek.parking.account.infrastructure.AccountRepository;
import com.niedzwiadek.parking.auth.api.AuthOperations;
import com.niedzwiadek.parking.car.infrastructure.CarRepository;
import com.niedzwiadek.parking.rest.account.AccountController;
import com.niedzwiadek.parking.rest.carpark.CarController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.google.gson.JsonParser.parseString;
import static com.niedzwiadek.parking.rest.account.AccountController.ACCOUNTS_PATH;
import static com.niedzwiadek.parking.rest.account.AccountController.ACCOUNT_PATH;
import static com.niedzwiadek.parking.rest.auth.AuthController.LOGIN_PATH;
import static com.niedzwiadek.parking.rest.auth.AuthController.REGISTER_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CARS_SEARCH_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CAR_FIND_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CAR_PATH;
import static com.niedzwiadek.parking.rest.carpark.CarController.CAR_UPDATE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ParkingApplicationIntegrationTest {

  static final String email = "email@pollub.pl";
  static final String password = "Password";

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CarRepository carRepository;
  @Autowired
  private AccountRepository accountRepository;

  @BeforeEach
  void beforeEach() {
    accountRepository.deleteAll();
    carRepository.deleteAll();
  }

  @Test
  void happyPathIntegrationTest() throws Exception {
    // ------> should register
    final var registerRequest = AuthOperations.RegisterRequest.builder()
        .name("Name")
        .email(email)
        .password(password)
        .build();
    mockMvc.perform(post(REGISTER_PATH)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    assertThat(accountRepository.findByEmail(email)).isNotEmpty();

    // ------> should login
    final var loginRequest = new AuthOperations.AuthenticationRequest(email, password);
    final var loginResponse = mockMvc.perform(post(LOGIN_PATH)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var jwtToken = parseString(loginResponse).getAsJsonObject().get("token").getAsString();

    // ------> should get account
    final var account = mockMvc.perform(get(ACCOUNTS_PATH)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var accountId = parseString(account).getAsJsonObject().get("id").getAsString();

    // ------> should update account
    final var accountUpdateDto = new AccountController.AccountUpdateDto("newName");
    mockMvc.perform(put(ACCOUNT_PATH, accountId)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(accountUpdateDto)))
        .andExpect(status().isOk());
    final var foundAccount = accountRepository.findByEmail(email);
    assertThat(foundAccount).isNotEmpty();
    assertThat(foundAccount.get().getName()).isEqualTo("newName");

    // ------> should add car
    final var registrationNumber1 = "registrationNumber";
    final var carCreateDto1 = CarController.CarCreateDto.builder()
        .registrationNumber(registrationNumber1)
        .arrivalDate(LocalDateTime.now())
        .country("Germany")
        .paid(false)
        .build();
    mockMvc.perform(post(CAR_PATH)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(carCreateDto1)))
        .andExpect(status().isOk());
    assertThat(carRepository.findByRegistrationNumberIgnoreCase(registrationNumber1)).isNotEmpty();

    // ------> should get car
    final var car = mockMvc.perform(get(CAR_FIND_PATH, registrationNumber1)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var carId = parseString(car).getAsJsonObject().get("carId").getAsString();

    // ------> should update car
    final var carUpdateDto = CarController.CarUpdateDto.builder()
        .country("Poland")
        .paid(true)
        .build();
    mockMvc.perform(patch(CAR_UPDATE_PATH, carId)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(carUpdateDto)))
        .andExpect(status().isOk());
    final var foundCar = carRepository.findByRegistrationNumberIgnoreCase(registrationNumber1);
    assertThat(foundCar).isNotEmpty();
    assertThat(foundCar.get().getCountry()).isEqualTo("Poland");
    assertThat(foundCar.get().isPaid()).isEqualTo(true);

    // ------> should add more cars
    final var registrationNumber2 = "nextCarNumber";
    final var carCreateDto2 = CarController.CarCreateDto.builder()
        .registrationNumber(registrationNumber2)
        .arrivalDate(LocalDateTime.now())
        .departureDate(LocalDateTime.now().plusHours(20))
        .country("Italy")
        .paid(false)
        .build();
    mockMvc.perform(post(CAR_PATH)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(carCreateDto2)))
        .andExpect(status().isOk());

    final var registrationNumber3 = "otherCarNumber";
    final var carCreateDto3 = CarController.CarCreateDto.builder()
        .registrationNumber(registrationNumber3)
        .build();
    mockMvc.perform(post(CAR_PATH)
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(carCreateDto3)))
        .andExpect(status().isOk());
    assertThat(carRepository.findAll()).hasSize(3);

    // ------> should search cars
    final var searchResponse1 = mockMvc.perform(get(CARS_SEARCH_PATH, "other")
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var jsonArray1 = parseString(searchResponse1).getAsJsonArray();
    assertThat(jsonArray1).hasSize(1);
    assertThat(jsonArray1.get(0).getAsJsonObject().get("registrationNumber").getAsString()).isEqualTo(registrationNumber3);

    final var searchResponse2 = mockMvc.perform(get(CARS_SEARCH_PATH, "Number")
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var jsonArray2 = parseString(searchResponse2).getAsJsonArray();
    assertThat(jsonArray2).hasSize(3);

    final var searchResponse3 = mockMvc.perform(get(CARS_SEARCH_PATH, "CarNumber")
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    final var jsonArray3 = parseString(searchResponse3).getAsJsonArray();
    assertThat(jsonArray3).hasSize(2);

    // ------> should delete account
    mockMvc.perform(delete(ACCOUNT_PATH, accountId)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk());
    assertThat(accountRepository.findByEmail(email)).isEmpty();
    assertThat(carRepository.findAll()).isEmpty();
  }
}
