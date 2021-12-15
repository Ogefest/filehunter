package com.ogefest.filehunter;

import java.io.File;
import java.util.Locale;
import java.util.zip.CRC32;

public class FileInfo {

    private String path;
    private DirectoryIndex index;
    private FileAttributes fileAttributes;

    public FileInfo(String path, DirectoryIndex index, FileAttributes fileAttributes) {
        this.path = path;
        this.index = index;
        this.fileAttributes = fileAttributes;
    }

    public String getHash() {
        CRC32 crc32 = new CRC32();
        crc32.update(path.getBytes());
        return String.format(Locale.US,"%08X", crc32.getValue());
    }

    public String getPath() {
        return  path;
    }

    public DirectoryIndex getIndex() {
        return index;
    }

    public boolean isFile() {
        return true;
    }

    public boolean isDirectory() {
        return true;
    }



}
