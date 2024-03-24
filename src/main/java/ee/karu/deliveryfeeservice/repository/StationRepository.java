package ee.karu.deliveryfeeservice.repository;

import ee.karu.deliveryfeeservice.model.City;
import ee.karu.deliveryfeeservice.model.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<StationEntity, Long> {

    Optional<StationEntity> getFirstByCityOrderByTimestampDesc(City city);
}
