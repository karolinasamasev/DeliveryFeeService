package ee.karu.deliveryfeeservice.controller;

import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.VehicleType;
import ee.karu.deliveryfeeservice.service.FeeCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/fees")
public class FeeController {
    public FeeController(FeeCalculationService feeCalculationService) {
        this.feeCalculationService = feeCalculationService;
    }

    private final FeeCalculationService feeCalculationService;

    @GetMapping("/calculate")
    public BigDecimal calculateFee(@RequestParam City city, @RequestParam VehicleType vehicleType) {
        return feeCalculationService.calculateFee(city, vehicleType);
    }

}
