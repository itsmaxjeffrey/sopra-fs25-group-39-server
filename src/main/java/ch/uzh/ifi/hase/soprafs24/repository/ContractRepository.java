package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import java.util.List;

@Repository("contractRepository")
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByRequester_UserId(Long userId);
    List<Contract> findByContractStatus(ContractStatus status);
}