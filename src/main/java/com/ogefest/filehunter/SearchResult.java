package com.ogefest.filehunter;

public class SearchResult {

    private String uuid;
    private String path;

    public SearchResult(String uuid, String path) {
        this.uuid = uuid;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getUuid() {
        return uuid;
    }


}
