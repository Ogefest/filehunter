package com.ogefest.filehunter;

import java.time.LocalDateTime;

public class FileAttributes {

    private long size = 0;
    private FileType type;
    private LocalDateTime lastModified;

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }
}
