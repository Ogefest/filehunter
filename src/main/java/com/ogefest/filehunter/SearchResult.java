package com.ogefest.filehunter;

import org.apache.lucene.document.Document;

public class SearchResult {

    private String uuid = "";
    private String path = "";
    private String type = "";
    private long size = 0;
    private String ext = "";
    private String indexname = "";
    private String name = "";

    public SearchResult(Document doc) {
        this.uuid = doc.get("uuid");
        this.path = doc.get("path");
        this.name = doc.get("name");
        this.type = doc.get("type");
        if (doc.get("size") != null) {
            this.size = Long.parseLong(doc.get("size"));
        }

        this.ext = doc.get("ext");
        this.indexname = doc.get("indexname");
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getUuid() {
        return uuid;
    }

    public long getSize() {
        return size;
    }

    public String getExt() {
        return ext;
    }

    public String getIndexname() {
        return indexname;
    }

    public String getName() {
        return name;
    }
}
