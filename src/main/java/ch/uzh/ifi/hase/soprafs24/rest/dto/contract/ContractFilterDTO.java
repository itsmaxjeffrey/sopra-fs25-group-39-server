package ch.uzh.ifi.hase.soprafs24.rest.dto.contract;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate moveDate;} 