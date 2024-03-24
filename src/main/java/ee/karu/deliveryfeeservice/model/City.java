package ee.karu.deliveryfeeservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Getter
@RequiredArgsConstructor
public enum City {
    TALLINN ("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PÄRNU("Pärnu");
    public final static Set<String> STATION_NAMES = getStationNames();

    private final String stationName;


    public static City getCityForStationName(String stationName) {
        return Arrays.stream(City.values())
                .filter(city -> city.stationName.equals(stationName))
                .findFirst()
                .orElseThrow();
    }

    private static Set<String> getStationNames() {
        return Arrays.stream(City.values()).map(City::getStationName).collect(toSet());
    }

}
