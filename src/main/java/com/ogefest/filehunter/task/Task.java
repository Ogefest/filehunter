package com.ogefest.filehunter.task;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.IndexRead;
import com.ogefest.filehunter.IndexWrite;

public abstract class Task {

//    private App app;
    private IndexWrite indexWrite;
    private IndexRead indexRead;
    private Configuration conf;

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
