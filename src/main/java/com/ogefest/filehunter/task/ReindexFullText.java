package com.ogefest.filehunter.task;

import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.FileInfoLucene;
import com.ogefest.filehunter.search.IndexWrite;
import com.ogefest.filehunter.storage.FTSStatus;
import com.ogefest.filehunter.storage.FileSystemDatabase;

import java.io.IOException;
import java.util.ArrayList;

public class ReindexFullText extends Task {

    private IndexWrite writer;
    private FileSystemDatabase db;

    @Override
    public void run() {
        db = getDatabase();
        writer = getIndexWrite();

        try {
            proceed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void proceed() throws IOException {
        ArrayList<FileInfo> items = db.getItemsToFullTextIndex();

        for (FileInfo fi : items) {
            FileInfoLucene fiLucene = new FileInfoLucene(fi);
            writer.addDocument(fiLucene);
            db.setCurrentFTSStatus(fi, FTSStatus.IN_INDEX.getValue());
        }
    }
}
