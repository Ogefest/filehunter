package com.ogefest.filehunter;

import java.io.File;
import java.util.Locale;
import java.util.zip.CRC32;

public class FileInfo {

    private String path;
    private String index;
    private FileAttributes fileAttributes;
    private int id;
    private int parentId;

    public FileInfo(int id, int parentId, String path, String index, FileAttributes fileAttributes) {
        this.id = id;
        this.parentId = parentId;
        this.path = path;
        this.index = index;
        this.fileAttributes = fileAttributes;
    }

    public String getHash() {
        CRC32 crc32 = new CRC32();
        crc32.update(path.getBytes());
        return String.format(Locale.US,"%08X", crc32.getValue());
    }

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean existsInDatabase() {
        return id > 0;
    }

    public String getPath() {
        return  path;
    }

    public String getIndexName() {
        return index;
    }

    public boolean isFile() {
        return true;
    }

    public boolean isDirectory() {
        return true;
    }



}
