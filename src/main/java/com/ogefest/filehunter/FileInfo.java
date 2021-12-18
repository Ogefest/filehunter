package com.ogefest.filehunter;

import java.time.LocalDateTime;

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

    public String getName() {
        String[] elems = path.split("/");

        return elems[elems.length - 1];
    }

    public String getExt() {
        String[] elems = path.split("\\.");

        if (elems.length < 2) {
            return "";
        }
        if (elems[elems.length - 1].length() > 5) {
            return "";
        }
        return elems[elems.length - 1];
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
        return path;
    }

    public String getIndexName() {
        return index;
    }

    public boolean isFile() {
        return fileAttributes.getType() == FileType.FILE;
    }

    public boolean isDirectory() {
        return fileAttributes.getType() == FileType.DIRECTORY;
    }

    public LocalDateTime getLastModified() {
        return fileAttributes.getLastModified();
    }

    public long getSize() {
        return fileAttributes.getSize();
    }


}
