package com.ogefest.filehunter;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class SystemStatus {

    private long docsNumber = 0;
    private String currentTask = "";

    @ConfigProperty(name = "quarkus.application.version")
    private String currentVersion;

    public long getDocsNumber() {
        return docsNumber;
    }

    public void setDocsNumber(long docsNumber) {
        this.docsNumber = docsNumber;
    }

    public String getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(String taskName) {
        this.currentTask = taskName;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
