package com.ogefest.filehunter;

import com.ogefest.filehunter.task.Task;

public class SystemStatus {
    private long docsNumber = 0;
    private String currentTask = "";

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
}
