package com.ogefest.filehunter;

import org.apache.lucene.document.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class FileInfoLucene {

    private String uuid;
    private String parentUuid;
    private String path;
    private LocalDateTime lastModified = LocalDateTime.of(1970, 1, 1, 0, 0);
    private LocalDateTime created = LocalDateTime.of(1970, 1, 1, 0, 0);
    private LocalDateTime lastMetaIndexed = LocalDateTime.of(1970, 1, 1, 0, 0);
    private String name;
    private String ext = "";
    private long size = 0;
    private FileType type;
    private String indexname;
    private String content = "";

    public FileInfoLucene(Document doc) {
        this.uuid = doc.get("ident");
        this.parentUuid = doc.get("parent");
        this.path = doc.get("path");
        this.name = doc.get("name");
        this.type = doc.get("type").equals("d") ? FileType.DIRECTORY : FileType.FILE;
        this.size = 0;
        if (doc.get("size") != null) {
            this.size = Long.parseLong(doc.get("size"));
        }

        this.ext = doc.get("ext");
        this.indexname = doc.get("indexname");
        this.lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(doc.get("last_modified"))), ZoneId.systemDefault());
//        this.created = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(doc.get("created"))), ZoneId.systemDefault());
        this.lastMetaIndexed = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(doc.get("metaindexed"))), ZoneId.systemDefault());
        this.content = doc.get("content");
    }

    public FileInfoLucene(FileInfo fi) {
        indexname = fi.getIndexName();
        type = fi.isDirectory() ? FileType.DIRECTORY : FileType.FILE;
        uuid = String.valueOf(fi.getId());
        ext = fi.getExt();
        size = fi.getSize();
        path = fi.getPath();
        name = fi.getName();
        lastModified = fi.getLastModified();
        lastMetaIndexed = fi.getLastMetaIndexed();
    }

    public FileInfo getFileInfo() {

        FileAttributes fa = new FileAttributes();
        fa.setLastModified(lastModified);
        fa.setType(type);
        fa.setSize(size);
        fa.setLastModified(lastMetaIndexed);

        FileInfo fi = new FileInfo(uuid, parentUuid, path, indexname, fa);

        return fi;
    }

    public String getUuid() {
        return uuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public LocalDateTime getCreated() {
        return created;
    }

//    public LocalDateTime getLastMetaIndexed() {
//        return lastMetaIndexed;
//    }

    public String getName() {
        return name;
    }

    public String getExt() {
        return ext;
    }

    public long getSize() {
        return size;
    }

    public FileType getType() {
        return type;
    }

    public String getIndexname() {
        return indexname;
    }

    public LocalDateTime getLastMetaIndexed() {
        return lastMetaIndexed;
    }

    public void setLastMetaIndexed(LocalDateTime lastMetaIndexed) {
        this.lastMetaIndexed = lastMetaIndexed;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
