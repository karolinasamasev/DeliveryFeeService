package ee.karu.deliveryfeeservice.service;

import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.VehicleType;

import java.math.BigDecimal;


public interface FeeCalculationService {
    BigDecimal calculateFee(City city, VehicleType vehicleType);
}
