package com.ogefest.filehunter;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RegisterForReflection
public class FileAttributes {

    private long size = 0;
    private String type;
    private String lastModified;

    public LocalDateTime getLastModified() {
        if (lastModified == null) {
            return LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        return LocalDateTime.parse(lastModified, DateTimeFormatter.ISO_DATE_TIME);
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileType getType() {
        if (type.equals(FileType.DIRECTORY.name())) {
            return FileType.DIRECTORY;
        }
        return FileType.FILE;
    }

    public void setType(FileType type) {
        this.type = type.name();
    }
}
