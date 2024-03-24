package ee.karu.deliveryfeeservice.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.StationEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StationDto {

    @JacksonXmlProperty(localName = "name")
    private String stationName;

    @JacksonXmlProperty(localName = "wmocode")
    private long wmoCode;

    @JacksonXmlProperty(localName = "phenomenon")
    private String phenomenon;

    @JacksonXmlProperty(localName = "airtemperature")
    private double airTemperature;

    @JacksonXmlProperty(localName = "windspeed")
    private double windSpeed;

    public StationEntity convertToEntity() {

        StationEntity stationEntity = new StationEntity();

        stationEntity.setCity(City.getCityForStationName(stationName));
        stationEntity.setWmoCode(wmoCode);
        stationEntity.setPhenomenon(phenomenon);
        stationEntity.setAirTemperature(airTemperature);
        stationEntity.setWindSpeed(windSpeed);

        return stationEntity;
    }
}

