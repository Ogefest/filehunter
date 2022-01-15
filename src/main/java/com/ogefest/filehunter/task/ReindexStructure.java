package com.ogefest.filehunter.task;

import com.ogefest.filehunter.*;
import com.ogefest.filehunter.index.DirectoryIndex;
import com.ogefest.filehunter.index.DirectoryIndexStorage;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.FileObject;
import com.ogefest.unifiedcloudfilesystem.ResourceAccessException;
import com.ogefest.unifiedcloudfilesystem.UnifiedCloudFileSystem;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class ReindexStructure extends Task {

    private static final Logger LOG = Logger.getLogger(Worker.class);
    private DirectoryIndex index;
    private DirectoryIndexStorage directoryIndexStorage;
    private FileSystemDatabase db;
    private int reindexTimestamp = 0;
    private UnifiedCloudFileSystem ucfs;

    public ReindexStructure(DirectoryIndex index, Configuration conf) {

        this.index = index;
        reindexTimestamp = (int) (Instant.now().getEpochSecond());
        directoryIndexStorage = new DirectoryIndexStorage(conf);

        EngineConfiguration ec = new EngineConfiguration(index.getConfiguration());

        ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine(index.getName(), BackendEngineFactory.get(index.getType(), ec));
    }

    @Override
    public void run() {
        this.db = getDatabase();
        LOG.info("Structure reindex for " + index.getName() + " started");

        FileObject rootPath = ucfs.getByPath(index.getName(), "/");

        index.setReindexSessionId(reindexTimestamp);
        directoryIndexStorage.setDirectory(index);

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

        db.closeReindexingSession(reindexTimestamp, index);

        LOG.info("Structure reindex for " + index.getName() + " finished");

    }

    protected void walk(FileObject item) throws IOException, ResourceAccessException {
        if (item.getEngineItem().isDirectory()) {

            for (String pathToCheck : index.getIgnorePath()) {
                if (pathToCheck.length() > 0 && item.getEngineItem().getPath().indexOf(pathToCheck) == 0) {
                    return;
                }
            }
            for (String patternToCheck : index.getIgnorePhrase()) {
                if (patternToCheck.length() > 0 && item.getEngineItem().getPath().indexOf(patternToCheck) != -1) {
                    return;
                }
            }

            LOG.info("Scanning " + item.getEngineName() + ":" + item.getEngineItem().getPath());
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

        FileInfo fi = db.get(obj.getEngineItem().getPath(), index);
        if (fi == null) {
            FileAttributes fa = new FileAttributes();
            fa.setSize(obj.getEngineItem().getSize());
            fa.setLastModified(obj.getEngineItem().getLastModified());
            fa.setType(obj.getEngineItem().isDirectory() ? FileType.DIRECTORY : FileType.FILE);
            db.add(obj.getEngineItem().getPath(), fa, index);
        } else {
            fi.getFileAttributes().setSize(obj.getEngineItem().getSize());
            fi.getFileAttributes().setLastModified(obj.getEngineItem().getLastModified());
            db.add(fi);
        }


//
//
//
//        if (!db.exists(obj.getEngineItem().getPath(), index)) {
//
//
////            if (fi != null) {
////                db.setCurrentStatus(fi, reindexTimestamp);
////            }
//        } else {
//            FileInfo fi = db.get(obj.getEngineItem().getPath(), index);
////            db.setCurrentStatus(fi, reindexTimestamp);
//            db.setCurrentAttributes(fi, fa);
//        }

    }


}
