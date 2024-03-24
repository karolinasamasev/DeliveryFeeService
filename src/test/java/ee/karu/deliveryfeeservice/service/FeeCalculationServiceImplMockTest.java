package ee.karu.deliveryfeeservice.service;

import ee.karu.deliveryfeeservice.exception.ExtremeWeatherConditionException;
import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.StationEntity;
import ee.karu.deliveryfeeservice.model.VehicleType;
import ee.karu.deliveryfeeservice.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeeCalculationServiceImplMockTest {

    @Mock
    private StationRepository stationRepository;
    private FeeCalculationServiceImpl feeCalculationService;

    @BeforeEach
    public void init() {
        feeCalculationService = new FeeCalculationServiceImpl(stationRepository);
    }

    @ParameterizedTest(name = "{index}: calculates total delivery fee for city={0}, vehicle={1}, air temperature={2}" +
            ", wind speed={3} and phenomenon={4} ")
    @CsvSource(textBlock = """
                TALLINN, CAR, -30, 21, Light snowfall, 4.00
                TALLINN, SCOOTER, -1, 10, Moderate sleet, 5.00
                TALLINN, BIKE, 14, 20, Moderate rain, 4.00
                TARTU, CAR, 0, 22, Light sleet, 3.50
                TARTU, SCOOTER, -11, 15, Drifting snow, 5.00
                TARTU, BIKE, -2.1, 4.7, Light snow shower, 4.00
                PÄRNU, CAR, -3, 15, Glaze, 3.00
                PÄRNU, SCOOTER, 0, 12, Heavy rain, 3.50
                PÄRNU, BIKE, -10.1, 9, Heavy snowfall, 4.00
            """)
    @DisplayName("Test calculates total delivery fee")
    public void calculateFeeTest(City city, VehicleType vehicleType, double airTemperature, double windSpeed, String phenomenon, BigDecimal fee) {
        StationEntity station = StationEntity.builder()
                .airTemperature(airTemperature)
                .windSpeed(windSpeed)
                .phenomenon(phenomenon)
                .build();
        when(stationRepository.getFirstByCityOrderByTimestampDesc(city))
                .thenReturn(Optional.of(station));

        assertThat(feeCalculationService.calculateFee(city, vehicleType))
                .isEqualTo(fee);
    }

    @ParameterizedTest(name = "{index}: checks if the exception is thrown for city={0}, vehicle={1}, air temperature={2}" +
            ", wind speed={3} and phenomenon={4} ")
    @CsvSource(textBlock = """
                TALLINN, SCOOTER, 14, 20, Thunder
                TALLINN, BIKE, -1, 25, Moderate sleet
                TARTU, SCOOTER, -11, 15, Glaze
                TARTU, BIKE, -2.1, 4.7, Thunderstorm
                PÄRNU, SCOOTER, 0, 12, Hail
                PÄRNU, BIKE, -10.1, 20.1, Heavy snowfall
            """)
    @DisplayName("Test checks if the exception is thrown")
    public void calculateFeeExceptionTest(City city, VehicleType vehicleType, double airTemperature, double windSpeed, String phenomenon) {
        StationEntity station = StationEntity.builder()
                .airTemperature(airTemperature)
                .windSpeed(windSpeed)
                .phenomenon(phenomenon)
                .build();
        when(stationRepository.getFirstByCityOrderByTimestampDesc(city))
                .thenReturn(Optional.of(station));

        assertThatExceptionOfType(ExtremeWeatherConditionException.class)
                .isThrownBy(() -> feeCalculationService.calculateFee(city, vehicleType))
                .withMessage("Usage of selected vehicle type is forbidden");
    }

}
