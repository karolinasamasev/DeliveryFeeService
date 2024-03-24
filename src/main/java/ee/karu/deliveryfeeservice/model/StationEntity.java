package ee.karu.deliveryfeeservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Setter
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name="station")
public class StationEntity {

    @Id
    @GeneratedValue
    private Long id;
    private City city;
    private long wmoCode;
    private String phenomenon;
    private double airTemperature;
    private double windSpeed;
    private long timestamp;

    @Override
    public String toString() {
        return "StationEntity{" +
                "id=" + id +
                ", city=" + city +
                ", wmoCode=" + wmoCode +
                ", phenomenon='" + phenomenon + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", timestamp=" + timestamp +
                '}';
    }
}
