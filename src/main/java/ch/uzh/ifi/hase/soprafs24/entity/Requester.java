package ch.uzh.ifi.hase.soprafs24.entity;



import javax.persistence.*;
import java.util.List;

@Entity
@Table(name ="REQUESTERS")
public class Requester extends User {

    @OneToMany(mappedBy="requester",fetch = FetchType.LAZY)
    private List<Contract> contracts;

    public List<Contract> getContracts(){
        return this.contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }
    
    public void addContract(Contract contract){
        this.contracts.add(contract);
    }

    public void removeContract(Contract contract){

        this.contracts.remove(contract);
        contract.setRequester(null);
    }


}
