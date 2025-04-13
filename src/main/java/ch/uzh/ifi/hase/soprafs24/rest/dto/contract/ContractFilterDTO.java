package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContractFilterDTO {
    private Double radius;
    private Double price;
    private Double weight;
    private Double height;
    private Double length;
    private Double width;
    private Integer requiredPeople;
    private Boolean fragile;
    private Boolean coolingRequired;
    private Boolean rideAlong;
    private String fromAddress;
    private String toAddress;
    private LocalDate moveDate;
} 