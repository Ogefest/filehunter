package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.DirectoryIndex;
import com.ogefest.filehunter.FileInfo;

import java.io.File;
import java.nio.file.Path;

public interface FileSystemDatabase {
    void add(FileInfo f);
    void clear(FileInfo fi);

    FileInfo get(String path, DirectoryIndex index);
    boolean exists(FileInfo fi);
    void setReindexCounter(FileInfo fi, int counter);
}
