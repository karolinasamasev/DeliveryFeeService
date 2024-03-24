package ee.karu.deliveryfeeservice.repository;

import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.StationEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StationRepositoryTest {

    @Autowired
    private StationRepository stationRepository;

    @Test
    @DisplayName("Checks if latest station entity is returned")
    public void getFirstByCityOrderByTimestampDescTest() {
        stationRepository.saveAll(List.of(
                createStationEntity(1L, City.TARTU, 1L),
                createStationEntity(2L, City.TALLINN, 1L),
                createStationEntity(3L, City.PÄRNU, 1L),
                createStationEntity(4L, City.TARTU, 2L),
                createStationEntity(5L, City.PÄRNU, 3L)
        ));

        assertThat(stationRepository.getFirstByCityOrderByTimestampDesc(City.TARTU))
                .isNotEmpty()
                .get()
                .extracting(StationEntity::getId)
                .isEqualTo(4L);
    }

    @Test
    @DisplayName("Checks whether nothing is returned when the search criteria is not matched")
    public void getFirstByCityOrderByTimestampDescEmptyDatabaseTest() {
        stationRepository.saveAll(List.of(
                createStationEntity(1L, City.TALLINN, 1L),
                createStationEntity(2L, City.PÄRNU, 1L),
                createStationEntity(3L, City.PÄRNU, 3L)
        ));

        assertThat(stationRepository.getFirstByCityOrderByTimestampDesc(City.TARTU))
                .isEmpty();
    }

    private static StationEntity createStationEntity(long id, City city, long timestamp) {
        return StationEntity.builder()
                .id(id)
                .city(city)
                .timestamp(timestamp)
                .build();
    }

}
