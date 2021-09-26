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

    public SearchResult(FileInfo finfo) {
        uuid = finfo.getUuid();
        path = finfo.getPath();
        name = finfo.getName();
        type = finfo.getType() == FileType.DIRECTORY ? "d" : "f";
        size = finfo.getSize();
        ext = finfo.getExt();
        indexname = finfo.getIndexname();
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
