package com.ogefest.filehunter;

import org.apache.lucene.document.Document;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

public class FileInfo {

    private String uuid;
    private String path;
    private LocalDateTime lastModified = LocalDateTime.of(1970, 1, 1,0,0);
    private LocalDateTime created = LocalDateTime.of(1970, 1, 1,0,0);
    private LocalDateTime lastMetaIndexed = LocalDateTime.of(1970, 1, 1,0,0);
    private String name;
    private String ext = "";
    private long size = 0;
    private FileType type;
    private String indexname;
    private String content = "";

    public FileInfo(Document doc) {
        this.uuid = doc.get("id");
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
        this.created = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(doc.get("created"))), ZoneId.systemDefault());
        this.lastMetaIndexed = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(doc.get("metaindexed"))), ZoneId.systemDefault());
        this.content = doc.get("content");
    }

    public FileInfo(Path inputPath, BasicFileAttributes basicFileAttributes, DirectoryIndex directoryIndex ) {
        indexname = directoryIndex.getName();

        type = basicFileAttributes.isDirectory() ? FileType.DIRECTORY : FileType.FILE;
        uuid = UUID.nameUUIDFromBytes(inputPath.toAbsolutePath().toString().getBytes()).toString().replace("-", "");

        Optional<String> opt = Optional.ofNullable(inputPath.getFileName().toString())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(inputPath.getFileName().toString().lastIndexOf(".") + 1));

        ext = "";
        if (opt.isPresent()) {
            ext = opt.get();
        }

        size = basicFileAttributes.size();
        path = inputPath.toAbsolutePath().toString();
        name = inputPath.getFileName().toString();
        lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(basicFileAttributes.lastModifiedTime().toMillis()), ZoneId.systemDefault());
        created = LocalDateTime.ofInstant(Instant.ofEpochMilli(basicFileAttributes.creationTime().toMillis()), ZoneId.systemDefault());

    }

    public String getUuid() {
        return uuid;
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
