package com.ogefest.filehunter;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.zip.CRC32;

public class FileItem {

    private String uid;
    private String name;
    private String path;
    private String index;
    private long size;
    private String ext;
    private String type;
    private String lastModified;


    public FileItem(FileInfo fi) {
        index = fi.getIndexName();
        path = fi.getPath();
        name = fi.getName();
        ext = fi.getExt();
        size = fi.getSize();
        lastModified = fi.getLastModified().format(DateTimeFormatter.ISO_DATE_TIME);
        uid = fi.getId() + "-" + fi.getHash();

        type = "f";
        if (fi.isDirectory()) {
            type = "d";
        }
    }

    public FileItem(FileInfoLucene finfo) {
        index = finfo.getIndexname();
        path = finfo.getPath();
        name = finfo.getName();
        ext = finfo.getExt();
        size = finfo.getSize();
        lastModified = finfo.getLastModified().format(DateTimeFormatter.ISO_DATE_TIME);
        type = "f";
        if (finfo.getType() == FileType.DIRECTORY) {
            type = "d";
        }

        CRC32 hash = new CRC32();
        hash.update(path.getBytes(StandardCharsets.UTF_8));
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
