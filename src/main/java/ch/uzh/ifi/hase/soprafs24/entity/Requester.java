package ch.uzh.ifi.hase.soprafs24.entity;



import javax.persistence.*;
import java.util.List;

@Entity
@Table(name ="REQUESTERS")
public class Requester extends User {

    @OneToMany(mappedBy="requester",fetch = FetchType.LAZY)
    private List<Contract> contracts;
}
