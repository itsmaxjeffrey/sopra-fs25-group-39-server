package ch.uzh.ifi.hase.soprafs24.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.constant.OfferStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByContract_ContractIdAndDriver_UserIdAndOfferStatus(Long contractId, Long userId, OfferStatus status);
    List<Offer> findByContract_ContractIdAndDriver_UserId(Long contractId, Long userId);
    List<Offer> findByContract_ContractIdAndOfferStatus(Long contractId, OfferStatus status);
    List<Offer> findByDriver_UserIdAndOfferStatus(Long userId, OfferStatus status);
    List<Offer> findByContract_ContractId(Long contractId);
    List<Offer> findByDriver_UserId(Long userId);
    List<Offer> findByOfferStatus(OfferStatus status);
} 