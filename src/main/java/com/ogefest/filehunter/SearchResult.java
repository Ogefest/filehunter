package com.ogefest.filehunter;

public class SearchResult {

    private String uuid;
    private String path;

    public String getName() {
        return name;
    }

    private String name;

    public SearchResult(String uuid, String path, String name) {
        this.uuid = uuid;
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getUuid() {
        return uuid;
    }


}
