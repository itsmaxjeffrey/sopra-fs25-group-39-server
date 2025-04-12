package ch.uzh.ifi.hase.soprafs24.contract.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.common.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.contract.model.Contract;

@Repository("contractRepository")
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByRequester_UserId(Long userId);
    List<Contract> findByDriver_UserId(Long userId);
    List<Contract> findByContractStatus(ContractStatus status);
    List<Contract> findByRequester_UserIdAndContractStatus(Long requesterId, ContractStatus status);
    List<Contract> findByDriver_UserIdAndContractStatus(Long driverId, ContractStatus status);
    List<Contract> findByContractStatusAndMoveDateTimeBefore(ContractStatus status, LocalDateTime dateTime);
}