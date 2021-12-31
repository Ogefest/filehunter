package com.ogefest.filehunter.task;

import com.ogefest.filehunter.BackendEngineFactory;
import com.ogefest.filehunter.FileAttributes;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.FileType;
import com.ogefest.filehunter.index.DirectoryIndex;
import com.ogefest.filehunter.search.IndexWrite;
import com.ogefest.filehunter.storage.FTSStatus;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.FileObject;
import com.ogefest.unifiedcloudfilesystem.ResourceAccessException;
import com.ogefest.unifiedcloudfilesystem.UnifiedCloudFileSystem;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class ReindexStructure extends Task {

    private DirectoryIndex index;
    private FileSystemDatabase db;
    private int reindexTimestamp = 0;
    private UnifiedCloudFileSystem ucfs;

    public ReindexStructure(DirectoryIndex index) {

        this.index = index;
        reindexTimestamp = (int) (Instant.now().getEpochSecond());

        EngineConfiguration ec = new EngineConfiguration(index.getConfiguration());

        ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine(index.getName(), BackendEngineFactory.get(index.getType(), ec));
    }

    @Override
    public void run() {
        this.db = getDatabase();

        FileObject rootPath = ucfs.getByPath(index.getName(), "/");

        db.openReindexingSession(reindexTimestamp, index);

        try {
            ArrayList<FileObject> isRootEmpty = ucfs.list(rootPath);
            if (isRootEmpty.size() == 0) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            return;
        }

        try {
            walk(rootPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResourceAccessException e) {
            e.printStackTrace();
        }

        IndexWrite writer = getIndexWrite();

        ArrayList<FileInfo> itemsToClear = db.getItemsToClear();
        for (FileInfo fi : itemsToClear) {
            writer.deleteDocument(String.valueOf(fi.getId()));
            db.clear(fi);
        }
//        ArrayList<FileInfo> itemsToReindex = db.getItemsToFullTextIndex();
//        for (FileInfo fi : itemsToReindex) {
//            /**
//             * @TODO add item to lucene
//             */
//        }
//        writer.closeIndex();

        db.closeReindexingSession(reindexTimestamp, index);

    }

    protected void walk(FileObject item) throws IOException, ResourceAccessException {
        if (item.getEngineItem().isDirectory()) {
            ArrayList<FileObject> itemsToCheck = ucfs.list(item);
            for (FileObject obj : itemsToCheck) {
                addToDatabase(obj);

                if (obj.getEngineItem().isDirectory()) {
                    if (!obj.getEngineItem().getPath().equals(item.getEngineItem().getPath())) {
                        walk(obj);
                    }
                }
            }
        }
    }

    protected void addToDatabase(FileObject obj) {

        if (index.getIgnoreExtension().contains(obj.getEngineItem().getExt())) {
            return;
        }
        for (String pathToCheck : index.getIgnorePath()) {
            if (obj.getEngineItem().getPath().indexOf(pathToCheck) == 0) {
                return;
            }
        }
        for (String patternToCheck : index.getIgnorePhrase()) {
            if (obj.getEngineItem().getPath().indexOf(patternToCheck) != -1) {
                return;
            }
        }

        FileAttributes fa = new FileAttributes();
        fa.setSize(obj.getEngineItem().getSize());
        fa.setLastModified(obj.getEngineItem().getLastModified());
        fa.setType(obj.getEngineItem().isDirectory() ? FileType.DIRECTORY : FileType.FILE);

        if (!db.exists(obj.getEngineItem().getPath(), index)) {

            FileInfo fi = db.add(obj.getEngineItem().getPath(), fa, index);
            if (fi != null) {
                db.setCurrentFTSStatus(fi, FTSStatus.TO_ADD.getValue());
                db.setCurrentStatus(fi, reindexTimestamp);
            }
        } else {
            FileInfo fi = db.get(obj.getEngineItem().getPath(), index);
            db.setCurrentStatus(fi, reindexTimestamp);
            db.setCurrentAttributes(fi, fa);
        }

    }


}
