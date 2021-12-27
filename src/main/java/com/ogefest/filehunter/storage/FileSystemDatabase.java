package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.index.DirectoryIndex;
import com.ogefest.filehunter.FileAttributes;
import com.ogefest.filehunter.FileInfo;

import java.util.ArrayList;

public interface FileSystemDatabase {
    void clear(FileInfo fi);

    FileInfo add(String path, FileAttributes attributes, DirectoryIndex index);

    FileInfo get(String path, DirectoryIndex index);

    ArrayList<FileInfo> list(String path, DirectoryIndex index);

    boolean exists(FileInfo fi);

    boolean exists(String path, DirectoryIndex index);

    void setCurrentStatus(FileInfo fi, int counter);

    void setCurrentFTSStatus(FileInfo fi, int ftsStatus);

    void setCurrentAttributes(FileInfo fi, FileAttributes attributes);

    ArrayList<FileInfo> getItemsToClear();

    ArrayList<FileInfo> getItemsToFullTextIndex();

    void openReindexingSession(int sessionId, DirectoryIndex index);

    void closeReindexingSession(int sessionId, DirectoryIndex index);

    void closeConnection();
}
