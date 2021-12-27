package com.ogefest.filehunter.index;

import java.util.HashMap;

public class Status {
    private HashMap<String, StatusType> status = new HashMap<>();

    public void set(String name, StatusType value) {
        this.status.put(name, value);
    }

    public StatusType get(String name) {
        if (this.status.containsKey(name)) {
            return this.status.get(name);
        }
        return StatusType.UNKNOWN;
    }
}
