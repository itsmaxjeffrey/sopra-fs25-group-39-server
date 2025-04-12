package ch.uzh.ifi.hase.soprafs24.common.constant;

public enum UserAccountType {
    DRIVER,
    REQUESTER;

    @Override
    public String toString(){
        return name().toLowerCase();
    }
}
