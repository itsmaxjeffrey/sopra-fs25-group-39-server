package ch.uzh.ifi.hase.soprafs24.entity;



import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="REQUESTERS")
@Getter @Setter
public class Requester extends User {

    @OneToMany(mappedBy="requester",fetch = FetchType.LAZY)
    private List<Contract> contracts;

    
    public void addContract(Contract contract){
        this.contracts.add(contract);
    }

    public void removeContract(Contract contract){

        this.contracts.remove(contract);
        contract.setRequester(null);
    }


}
