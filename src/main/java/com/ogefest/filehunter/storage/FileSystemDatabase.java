package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.DirectoryIndex;
import com.ogefest.filehunter.FileAttributes;
import com.ogefest.filehunter.FileInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

public interface FileSystemDatabase {
    void add(FileInfo f, int indexCounter);
    void clear(FileInfo fi);

    FileInfo get(String path, DirectoryIndex index);
    ArrayList<FileInfo> list(String path, DirectoryIndex index);
    boolean exists(FileInfo fi);

    void setCurrentStatus(FileInfo fi, int counter);
    void setCurrentFTSStatus(FileInfo fi, int ftsStatus);

    ArrayList<FileInfo> getItemsToClear();
    ArrayList<FileInfo> getItemsToFullTextIndex();

    void openReindexingSession(int sessionId, DirectoryIndex index);
    void closeReindexingSession(int sessionId, DirectoryIndex index);

    void closeConnection();
}
