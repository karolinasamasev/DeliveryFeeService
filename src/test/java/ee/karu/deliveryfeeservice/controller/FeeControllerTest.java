package ee.karu.deliveryfeeservice.controller;

import ee.karu.deliveryfeeservice.exception.ExtremeWeatherConditionException;
import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.VehicleType;
import ee.karu.deliveryfeeservice.service.FeeCalculationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeeController.class)
public class FeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeeCalculationService feeCalculationService;

    @Test
    @DisplayName("Checks interaction between controller and service")
    public void calculateFeeTest() throws Exception {
        when(feeCalculationService.calculateFee(City.TARTU, VehicleType.CAR))
                .thenReturn(new BigDecimal("3.50"));

        mockMvc.perform(get("/api/v1/fees/calculate")
                        .param("city", City.TARTU.name())
                        .param("vehicleType", VehicleType.CAR.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(3.5));
    }


    @Test
    @DisplayName("Checks whether the exception is correctly handled")
    public void handleExceptionTest() throws Exception {
        String exceptionMessage = "Usage of selected vehicle type is forbidden";
        when(feeCalculationService.calculateFee(City.TARTU, VehicleType.BIKE))
                .thenThrow(new ExtremeWeatherConditionException(exceptionMessage));

        mockMvc.perform(get("/api/v1/fees/calculate")
                        .param("city", City.TARTU.name())
                        .param("vehicleType", VehicleType.BIKE.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(exceptionMessage))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()));
    }

}
