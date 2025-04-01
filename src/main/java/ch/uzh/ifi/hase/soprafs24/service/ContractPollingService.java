package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs24.constant.ContractStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Contract;
import ch.uzh.ifi.hase.soprafs24.repository.ContractRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ContractPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.ContractDTOMapper;


@RestController
@RequestMapping("/contracts")
public class ContractPollingService {

    private final ContractService contractService;
    private final ContractRepository contractRepository;
    private final List<CompletableFuture<List<ContractGetDTO>>> waitingClients = new CopyOnWriteArrayList<>();

    public ContractPollingService(ContractService contractService, ContractRepository contractRepository) {
        this.contractService = contractService;
        this.contractRepository = contractRepository;
    }

    // Long-polling endpoint for fetching new contracts
    @GetMapping("/poll")
    public CompletableFuture<List<ContractGetDTO>> pollNewContracts() {
        CompletableFuture<List<ContractGetDTO>> future = new CompletableFuture<>();

        List<Contract> newContracts = contractRepository.findByContractStatus(ContractStatus.REQUESTED);

        if (!newContracts.isEmpty()) {
            future.complete(newContracts.stream()
                .map(ContractDTOMapper.INSTANCE::convertContractEntityToContractGetDTO)
                .collect(Collectors.toList()));
        } else {
            waitingClients.add(future);
            CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS).execute(() -> {
                if (!future.isDone()) {
                    future.complete(List.of());
                }
                waitingClients.remove(future);
            });
        }

        return future;
    }

    // API endpoint for adding new contracts
    @PostMapping("/add")
    public ContractGetDTO addNewContract(@RequestBody ContractPostDTO contractPostDTO) {
        Contract contract = ContractDTOMapper.INSTANCE.convertContractPostDTOtoEntity(contractPostDTO);
        Contract createdContract = contractService.createContract(contract);

        ContractGetDTO contractGetDTO = ContractDTOMapper.INSTANCE.convertContractEntityToContractGetDTO(createdContract);

        // Notify waiting clients
        for (CompletableFuture<List<ContractGetDTO>> client : waitingClients) {
            client.complete(List.of(contractGetDTO));
        }

        waitingClients.clear();

        return contractGetDTO;
    }
}
