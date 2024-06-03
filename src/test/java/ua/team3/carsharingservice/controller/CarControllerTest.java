package ua.team3.carsharingservice.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import ua.team3.carsharingservice.dto.CarDto;
import ua.team3.carsharingservice.dto.CreateCarRequestDto;
import ua.team3.carsharingservice.service.CarService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {
    private static final String BMW_BRAND = "BMW";
    private static final Long CAR_ID = 1L;
    private static final String EXCEPTED_CAR_TYPE_SEDAN = "SEDAN";
    private static final int EXPECTED_INVENTORY = 10;
    private static final String CARS_URL = "/cars";
    private static final String CARS_BY_ID_URL = "/cars/{cardId}";

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private CarService carService;

    private CreateCarRequestDto createCarRequestDto;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        createCarRequestDto = new CreateCarRequestDto();
        createCarRequestDto.setBrand(BMW_BRAND);
        createCarRequestDto.setInventory(10);
        createCarRequestDto.setType("SEDAN");
        createCarRequestDto.setDailyFee(BigDecimal.valueOf(100));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Create new car")
    void createCar_validDate_success() throws Exception {
        CarDto expectedCar = new CarDto();
        expectedCar.setId(CAR_ID);
        expectedCar.setType(EXCEPTED_CAR_TYPE_SEDAN);
        expectedCar.setBrand(BMW_BRAND);
        expectedCar.setInventory(EXPECTED_INVENTORY);
        expectedCar.setDailyFee(BigDecimal.valueOf(100));

        String jsonRequest = objectMapper.writeValueAsString(createCarRequestDto);
        String result = mockMvc.perform(post(CARS_URL)
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CarDto actual = objectMapper.readValue(result, CarDto.class);
        assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expectedCar, actual);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void findAllCars_ValidData_ListOfCars() throws Exception {
        List<CarDto> carDtoList = List.of(new CarDto());
        when(carService.findAllCars(any(Pageable.class))).thenReturn(carDtoList);
        mockMvc.perform(get(CARS_URL))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void deleteCarById_validData_success() throws Exception {
        mockMvc.perform(delete(CARS_BY_ID_URL, CAR_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("Delete book by id with invalid role")
    void deleteCarById_WIthInValidRoleUser_fail() throws Exception {
        mockMvc.perform(delete(CARS_BY_ID_URL, CAR_ID))
                .andExpect(status().isForbidden());
    }
}
