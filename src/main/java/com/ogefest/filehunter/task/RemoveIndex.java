package com.ogefest.filehunter.task;

public class RemoveIndex extends Task {

    private String name;

    public RemoveIndex(String name) {
        this.name = name;
    }

    @Override
    public void run() {
//        IndexWrite iw = getIndexWrite();
//        iw.deleteDocumentByDirectoryName(name);
//        iw.closeIndex();
    }
}
