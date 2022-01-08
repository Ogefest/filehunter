package com.ogefest.filehunter;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.zip.CRC32;

@RegisterForReflection
public class FileInfo {

    private String path;
    private String index;
    private FileAttributes fileAttributes;
    private FileMeta fileMeta;
    private String id;
    private String parentId;
    private long hash;

    public FileInfo(String id, String parentId, String path, String index, FileAttributes fileAttributes) {
        this.id = id;
        this.parentId = parentId;
        this.path = path;
        this.index = index;

        CRC32 hash = new CRC32();
        hash.update(path.getBytes(StandardCharsets.UTF_8));

        this.hash = hash.getValue();
        this.fileAttributes = fileAttributes;
    }

    public FileInfo(FileInfoLucene finfo) {
        this.id = finfo.getUuid();
        this.parentId = finfo.getParentUuid();
        this.path = finfo.getPath();
        this.index = finfo.getIndexname();

        FileAttributes fa = new FileAttributes();
        fa.setType(finfo.getType());
        fa.setSize(finfo.getSize());
        fa.setLastModified(finfo.getLastModified());
        fa.setLastMetaIndexed(finfo.getLastMetaIndexed());

        this.fileAttributes = fa;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean existsInDatabase() {
        return id.length() > 0;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public LocalDateTime getLastMetaIndexed() {
        return fileAttributes.getLastMetaIndexed();
    }

    public long getSize() {
        return fileAttributes.getSize();
    }

    public long getHash() {
        return hash;
    }

    public void setHash(long hash) {
        this.hash = hash;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public FileAttributes getFileAttributes() {
        return fileAttributes;
    }

    public void setFileAttributes(FileAttributes fileAttributes) {
        this.fileAttributes = fileAttributes;
    }
}
