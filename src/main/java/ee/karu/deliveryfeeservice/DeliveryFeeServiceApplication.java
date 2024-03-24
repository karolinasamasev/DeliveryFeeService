package ee.karu.deliveryfeeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class DeliveryFeeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryFeeServiceApplication.class, args);
    }
}
