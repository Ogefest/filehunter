package com.ogefest.filehunter.task;

import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.search.IndexWrite;
import com.ogefest.filehunter.storage.FileSystemDatabase;

import java.io.File;

public abstract class Task {

//    private App app;
    private IndexWrite indexWrite;
    private IndexRead indexRead;
    private Configuration conf;
    private FileSystemDatabase db;

//    public void setApp(App app) {
//        this.app = app;
//    }

    public void setConfiguration(Configuration conf) {
        this.conf = conf;
    }

    public Configuration getConfiguration() {
        return conf;
    }

    public void setIndexes(IndexWrite write, IndexRead read) {
        this.indexWrite = write;
        this.indexRead = read;
    }

    public void setDatabase(FileSystemDatabase db) {
        this.db = db;
    }

    protected FileSystemDatabase getDatabase() {
        return db;
    }

    public String getTaskName() {
        return this.getClass().getCanonicalName();
    }

//    protected App getApp() {
//        return app;
//    }

    public IndexWrite getIndexWrite() {
        return indexWrite;
    }

    public IndexRead getIndexRead() {
        return indexRead;
    }

    abstract public void run();
}
