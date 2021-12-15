package com.ogefest.filehunter;

import java.util.HashMap;

public class FileAttributes {

    private long size = 0;
    private FileType type;

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
