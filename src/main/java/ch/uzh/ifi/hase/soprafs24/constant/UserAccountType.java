package ch.uzh.ifi.hase.soprafs24.constant;

public enum UserAccountType {
    DRIVER,
    REQUESTER;

    @Override
    public String toString(){
        return name().toLowerCase();
    }
}
