package ee.karu.deliveryfeeservice.service;

import ee.karu.deliveryfeeservice.exception.ExtremeWeatherConditionException;
import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class FeeCalculationServiceImplUnitTest {

    @ParameterizedTest(name = "{index}: calculates regional base fee (RBF) for city={0} and vehicle={1} ")
    @CsvSource(textBlock = """
                TALLINN, CAR, 4.00
                TALLINN, SCOOTER, 3.50
                TALLINN, BIKE, 3.00
                TARTU, CAR, 3.50
                TARTU, SCOOTER, 3.00
                TARTU, BIKE, 2.50
                PÄRNU, CAR,  3.00
                PÄRNU, SCOOTER,  2.50
                PÄRNU, BIKE, 2.00
            """)
    @DisplayName("Test calculates regional base fee (RBF) in case City is Tallinn, Tartu or Pärnu")
    public void calculateRbfTest(City city, VehicleType vehicleType, BigDecimal fee) {
        assertThat(FeeCalculationServiceImpl.calculateRBF(city, vehicleType))
                .isEqualTo(fee);
    }

    @ParameterizedTest(name = "{index}: calculates extra fee based on air temperature (ATEF) for vehicle={0} and air temperature={1} ")
    @CsvSource(textBlock = """
                SCOOTER, -10.1, 1.00
                SCOOTER, -10, 0.50
                SCOOTER, -5, 0.50
                SCOOTER, 0, 0.50
                SCOOTER, 0.1, 0.00
                BIKE, -10.1, 1.00
                BIKE, -10, 0.50
                BIKE, -5, 0.50
                BIKE, 0, 0.50
                BIKE, 0.1, 0.00
            """)
    @DisplayName("Test calculates extra fee based on air temperature (ATEF)")
    public void calculateAtefTest(VehicleType vehicleType, double airTemperature, BigDecimal fee) {
        assertThat(FeeCalculationServiceImpl.calculateATEF(vehicleType, airTemperature))
                .isEqualTo(fee);
    }

    @ParameterizedTest(name = "{index}: calculates extra fee based on wind speed (WSEF) for vehicle={0} and wind speed={1} ")
    @CsvSource(textBlock = """
                BIKE, 10, 0.50
                BIKE, 10.1, 0.50
                BIKE, 19.9, 0.50
                BIKE, 20, 0.50
            """)
    @DisplayName("Test calculates extra fee based on wind speed (WSEF)")
    public void calculateWsefTest(VehicleType vehicleType, double windSpeed, BigDecimal fee) {
        assertThat(FeeCalculationServiceImpl.calculateWSEF(vehicleType, windSpeed))
                .isEqualTo(fee);
    }

    @ParameterizedTest(name = "{index}: checks if the exception is thrown for vehicle={0} and wind speed={1} ")
    @CsvSource(textBlock = """
                        BIKE, 20.1
                        BIKE, 25
            """)
    @DisplayName("Test checks if the exception is thrown in case of wind speed is greater than 20 m/s")
    public void calculateWsefThrowsExceptionTest(VehicleType vehicleType, double windSpeed) {
        assertThatExceptionOfType(ExtremeWeatherConditionException.class)
                .isThrownBy(() -> FeeCalculationServiceImpl.calculateWSEF(vehicleType, windSpeed))
                .withMessage("Usage of selected vehicle type is forbidden");
    }

    @ParameterizedTest(name = "{index}: calculates extra fee based on weather phenomenon (WPEF) for vehicle={0} and phenomenon={1} ")
    @CsvSource(textBlock = """
                SCOOTER, Blowing snow, 1.00
                SCOOTER, Light sleet, 1.00
                SCOOTER, Heavy rain, 0.50
                BIKE, Snowstorm, 1.00
                BIKE, Moderate sleet, 1.00
                BIKE, Light rain, 0.50
            """)
    @DisplayName("Test calculates extra fee based on weather phenomenon (WPEF)")
    public void calculateWpefTest(VehicleType vehicleType, String phenomenon, BigDecimal fee) {
        assertThat(FeeCalculationServiceImpl.calculateWPEF(vehicleType, phenomenon))
                .isEqualTo(fee);
    }

    @ParameterizedTest(name = "{index}: checks if the exception is thrown for vehicle={0} and phenomenon={1} ")
    @CsvSource(textBlock = """
                SCOOTER, Glaze
                SCOOTER, Hail
                SCOOTER, Thunder
                BIKE, Glaze
                BIKE, Hail
                BIKE, Thunder
            """)
    @DisplayName("Test checks if the exception is thrown in case the weather phenomenon is glaze, hail, or thunder")
    public void calculateWpefThrowsExceptionTest(VehicleType vehicleType, String phenomenon) {
        assertThatExceptionOfType(ExtremeWeatherConditionException.class)
                .isThrownBy(() -> FeeCalculationServiceImpl.calculateWPEF(vehicleType, phenomenon))
                .withMessage("Usage of selected vehicle type is forbidden");
    }
}
