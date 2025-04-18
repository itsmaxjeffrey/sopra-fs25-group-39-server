package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    // This class is intentionally left empty. The JpaRepository interface provides all the necessary methods for CRUD operations.
    // You can add custom query methods here if needed.
    Rating findByRatingId(Long ratingId);
}
