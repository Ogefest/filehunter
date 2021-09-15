package com.ogefest.filehunter.task;

import com.ogefest.filehunter.App;

public abstract class Task {

    private App app;
    public void setApp(App app) {
        this.app = app;
    }

    public String getTaskName() {
        return this.getClass().getCanonicalName();
    }

    protected App getApp() {
        return app;
    }

    abstract public void run();
}
