package ch.uzh.ifi.hase.soprafs24.location.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.location.model.Location;

@Repository("locationRepository")
public interface LocationRepository extends JpaRepository<Location, Long> {
}