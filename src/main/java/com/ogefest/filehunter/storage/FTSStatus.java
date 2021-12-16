package com.ogefest.filehunter.storage;

public enum FTSStatus {
    NOT_IN_INDEX(0),
    IN_INDEX(1),
    TO_ADD(2),
    TO_REMOVE(3);

    FTSStatus(final int newValue) {
        value = newValue;
    }

    private final int value;
    public int getValue() { return value; }
}
