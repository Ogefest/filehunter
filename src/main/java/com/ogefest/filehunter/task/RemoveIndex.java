package com.ogefest.filehunter.task;

import com.ogefest.filehunter.IndexWrite;

public class RemoveIndex extends Task {

    private String name;

    public RemoveIndex(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        IndexWrite iw = getIndexWrite();
        iw.deleteDocumentByDirectoryName(name);
        iw.closeIndex();
    }
}
