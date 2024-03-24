
package ee.karu.deliveryfeeservice.service;

import ee.karu.deliveryfeeservice.dto.ObservationDto;
import ee.karu.deliveryfeeservice.dto.StationDto;
import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class WeatherDataService {

    private final static String URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    private final RestClient restClient;
    private final StationRepository stationRepository;

    public WeatherDataService(RestClient restClient, StationRepository stationRepository) {
        this.restClient = restClient;
        this.stationRepository = stationRepository;
    }

    /**
     * This method fetches weather data from a remote API, processes it, and saves relevant station data to the database.
     * The retrieved observation data includes information about stations (cities and weather conditions)
     * and their observations (timestamps)
     * Only stations whose names are included in the predefined list of station names will be saved.
     * The timestamp of the observation data is added to each station entity before saving.
     * This method is scheduled to run at regular intervals based on the configured cron job frequency defined in appliaction.yaml.
     */
    @Scheduled(cron = "${cronjob.frequency}")
    public void getData() {
        ObservationDto observationDto = restClient.get()
                .uri(URL)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .body(ObservationDto.class);

        long timestamp = observationDto.getTimestamp();
        observationDto.getStations().stream()
                .filter(WeatherDataService::isShouldBeSaved)
                .map(StationDto::convertToEntity)
                .map(stationEntity -> stationEntity.toBuilder().timestamp(timestamp).build())
                .forEach(stationEntity -> {log.debug("Saving entity: {}", stationEntity);
                stationRepository.save(stationEntity);
                });
    }

    /**
     * Determines whether the provided station should be saved.
     *
     * @param stationDto The StationDto object.
     * @return {@code true} if the station should be saved, {@code false} otherwise.
     */
    private static boolean isShouldBeSaved(StationDto stationDto) {
        return City.STATION_NAMES.contains(stationDto.getStationName());
    }

}
