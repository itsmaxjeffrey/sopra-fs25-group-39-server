package ch.uzh.ifi.hase.soprafs24.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name ="REQUESTERS")
@Getter @Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "contracts")
public class Requester extends User {

    @OneToMany(mappedBy="requester",fetch = FetchType.LAZY)
    private List<Contract> contracts = new ArrayList<>();
    
    public void addContract(Contract contract){
        this.contracts.add(contract);
        contract.setRequester(this);
    }

    public void removeContract(Contract contract){
        this.contracts.remove(contract);
        contract.setRequester(null);
    }
}
