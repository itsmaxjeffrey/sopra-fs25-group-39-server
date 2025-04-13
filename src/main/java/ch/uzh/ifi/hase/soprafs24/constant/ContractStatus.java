package ch.uzh.ifi.hase.soprafs24.constant;

public enum ContractStatus {
    REQUESTED,
    DELETED,
    OFFERED,
    ACCEPTED,
    CANCELED,
    COMPLETED,
    FINALIZED;

    @Override
    public String toString(){
        return name().toLowerCase();
    }
}
