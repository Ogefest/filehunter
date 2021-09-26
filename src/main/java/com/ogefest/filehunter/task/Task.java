package com.ogefest.filehunter.task;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.IndexRead;
import com.ogefest.filehunter.IndexWrite;

public abstract class Task {

    private App app;
    private IndexWrite indexWrite;
    private IndexRead indexRead;

    public void setApp(App app) {
        this.app = app;
    }

    public void setIndexes(IndexWrite write, IndexRead read) {
        this.indexWrite = write;
        this.indexRead = read;
    }

    public String getTaskName() {
        return this.getClass().getCanonicalName();
    }

    protected App getApp() {
        return app;
    }

    public IndexWrite getIndexWrite() {
        return indexWrite;
    }

    public IndexRead getIndexRead() {
        return indexRead;
    }

    abstract public void run();
}
