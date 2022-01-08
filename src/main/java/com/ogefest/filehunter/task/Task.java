package com.ogefest.filehunter.task;

import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.filehunter.storage.LuceneSearch;

public abstract class Task {

    private LuceneSearch luceneSearch;
    private Configuration conf;
    private FileSystemDatabase db;

    public Configuration getConfiguration() {
        return conf;
    }

    public void setConfiguration(Configuration conf) {
        this.conf = conf;
    }

    protected FileSystemDatabase getDatabase() {
        return db;
    }

    public void setDatabase(FileSystemDatabase db) {
        this.db = db;
    }

    public String getTaskName() {
        return this.getClass().getCanonicalName();
    }

    abstract public void run();
}
