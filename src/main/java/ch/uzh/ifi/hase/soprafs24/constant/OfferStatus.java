package ch.uzh.ifi.hase.soprafs24.constant;

public enum OfferStatus {
    CREATED,
    DELETED,
    REJECTED,
    ACCEPTED;

    @Override
    public String toString(){
        return name().toLowerCase();
    }   
}
