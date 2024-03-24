package ee.karu.deliveryfeeservice.service;

import ee.karu.deliveryfeeservice.exception.ExtremeWeatherConditionException;
import ee.karu.deliveryfeeservice.exception.NoSuchObservationDataException;
import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.StationEntity;
import ee.karu.deliveryfeeservice.model.VehicleType;
import ee.karu.deliveryfeeservice.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class FeeCalculationServiceImpl implements FeeCalculationService {

    private final StationRepository stationRepository;

    public FeeCalculationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }


    /**
     * Calculates the total fee based on the given city and vehicle type, adding together
     * regional base fee (RBF), extra fee based on air temperature (ATEF),
     * extra fee based on wind speed (WPEF), and extra fee based on phenomenon (WSEF).
     *
     * @param city  The given city.
     * @param vehicleType The type of vehicle.
     * @return The total fee calculated based on the provided factors.
     * @throws NoSuchObservationDataException If observation data for the specified city is not available.
     */
    @Override
    public BigDecimal calculateFee(City city, VehicleType vehicleType) {

        log.debug("Calculating fee for city:{} and vehicle:{}", city, vehicleType);

        StationEntity stationEntity = stationRepository.getFirstByCityOrderByTimestampDesc(city)
                .orElseThrow(NoSuchObservationDataException::new);

        double airTemperature = stationEntity.getAirTemperature();
        double windSpeed = stationEntity.getWindSpeed();
        String phenomenon = stationEntity.getPhenomenon();

        log.debug("Recent weather data for {} is air temperature={}, wind speed={} and phenomenon={}", city, airTemperature, windSpeed, phenomenon);

        return calculateRBF(city, vehicleType)
                .add(calculateATEF(vehicleType, airTemperature)
                .add(calculateWPEF(vehicleType, phenomenon))
                .add(calculateWSEF(vehicleType, windSpeed)));

    }

    /**
     * Calculates the regional base fee (RBF) based on the specified city and vehicle type.
     * @param city The given city.
     * @param vehicleType The type of vehicle.
     * @return The calculated regional base fee in euros.
     */
    public static BigDecimal calculateRBF(City city, VehicleType vehicleType) {
        return switch (city) {
            case TALLINN ->
                switch (vehicleType) {
                    case CAR -> new BigDecimal("4.00");
                    case SCOOTER -> new BigDecimal("3.50");
                    case BIKE -> new BigDecimal("3.00");
                };
            case TARTU ->
                switch (vehicleType) {
                    case CAR -> new BigDecimal("3.50");
                    case SCOOTER ->  new BigDecimal("3.00");
                    case BIKE ->  new BigDecimal("2.50");
                };
            case PÄRNU ->
                switch (vehicleType) {
                    case CAR -> new BigDecimal("3.00");
                    case SCOOTER -> new BigDecimal("2.50");
                    case BIKE -> new BigDecimal("2.00");
                };
        };
    }

    /**
     * Calculates the ATEF based on the specified vehicle type and air temperature.
     * If the vehicle type is a car, returns 0.00.
     * For another two vehicle types, the method evaluates the air temperature and assigns an ATEF accordingly:
     * If the air temperature is below -10 degrees Celsius, ATEF is set to 1.00.
     * If the air temperature is between -10 and 0 degrees Celsius (inclusive), ATEF is set to 0.50.
     * If the air temperature does not match any of the conditions above, ATEF is set to 0.00.
     *
     * @param vehicleType The type of vehicle.
     * @param airTemperature The air temperature.
     * @return The calculated extra fee based on air temperature.
     */
    public static BigDecimal calculateATEF(VehicleType vehicleType, double airTemperature) {
        if (vehicleType == VehicleType.CAR) {
            return new BigDecimal("0.00");
        }
        if (airTemperature < -10) {
            return new BigDecimal("1.00");
        } else if (airTemperature <= 0) {
            return new BigDecimal("0.50");
        }
        return new BigDecimal("0.00");
    }

    /**
     * Calculates the WSEF based on the specified vehicle type and wind speed.
     * If the vehicle type is not a bike, returns 0.00.
     * For vehicle type BIKE, the method evaluates the wind speed and assigns a WSEF accordingly:
     * If the wind speed is between 10 and 20 km/h (inclusive), WSEF is set to 0.50.
     * If the wind speed exceeds 20 km/h, an ExtremeWeatherConditionException is thrown,
     *   indicating that the usage of bikes is forbidden due to extreme weather conditions.
     * If the wind speed does not match any of the conditions above, WSEF is set to 0.00.
     *
     * @param vehicleType The type of vehicle.
     * @param windSpeed   The wind speed.
     * @return The calculated extra fee based on wind speed.
     * @throws ExtremeWeatherConditionException "Usage of selected vehicle type is forbidden" is given.
     */
    public static BigDecimal calculateWSEF(VehicleType vehicleType, double windSpeed) {
        if (vehicleType != VehicleType.BIKE) {
            return new BigDecimal("0.00");
        }
        if (windSpeed >= 10 && windSpeed <= 20) {
            return new BigDecimal("0.50");
        } else if (windSpeed > 20) {
            throw new ExtremeWeatherConditionException("Usage of selected vehicle type is forbidden");
        }
        return new BigDecimal("0.00");
    }

    /**
     * Calculates the WPEF based on the specified vehicle type and weather phenomenon.
     * If the vehicle type is CAR, returns 0.00.
     * For another two vehicle types, the method evaluates the weather phenomenon and assigns a WPEF accordingly:
     * If the phenomenon includes "snow" or "sleet", WPEF is set to 1.00.
     * If the phenomenon includes "rain", WPEF is set to 0.50.
     * If the phenomenon includes "glaze", "hail", or "thunder", an ExtremeWeatherConditionException is thrown,
     *   indicating that the usage of the selected vehicle type is forbidden.
     * If the weather phenomenon does not match any of the conditions above, WPEF is set to 0.00.
     *
     * @param vehicleType The type of vehicle.
     * @param phenomenon  The weather phenomenon.
     * @return The calculated extra fee based on weather phenomenon.
     * @throws ExtremeWeatherConditionException "Usage of selected vehicle type is forbidden" is given.
     */
    public static BigDecimal calculateWPEF(VehicleType vehicleType, String phenomenon) {
        if (vehicleType == VehicleType.CAR) {
            return new BigDecimal("0.00");
        }
        String phenomenonLowerCased = phenomenon.toLowerCase();

        if (phenomenonLowerCased.contains("snow")
                || phenomenonLowerCased.contains("sleet")) {
            return new BigDecimal("1.00");
        }
        if (phenomenonLowerCased.contains("rain")) {
            return new BigDecimal("0.50");
        }
        if (phenomenonLowerCased.contains("glaze")
                || phenomenonLowerCased.contains("hail")
                || phenomenonLowerCased.contains("thunder")) {
            throw new ExtremeWeatherConditionException("Usage of selected vehicle type is forbidden");
        }
        return new BigDecimal("0.00");
    }

}
