package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.Rating;
import java.util.List;

@Repository("ratingRepository")
public interface RatingRepository extends JpaRepository<Rating, Long> {
    // This class is intentionally left empty. The JpaRepository interface provides all the necessary methods for CRUD operations.
    // You can add custom query methods here if needed.
    Rating findByRatingId(Long ratingId);
    List<Rating> findByToUser_UserId(Long userId);
    List<Rating> findByContract_ContractId(Long contractId);
    Rating findByContract_ContractIdAndFromUser_UserId(Long contractId, Long fromUserId);
}
