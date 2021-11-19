package com.ogefest.filehunter.task;

import com.ogefest.filehunter.*;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.search.IndexWrite;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class IndexStructure extends Task {

    private IndexWrite indexStorage;
    private IndexRead indexRead;
    private DirectoryIndex directoryIndex;

    private HashMap<String, String> fsStructure = new HashMap<>();
    private HashMap<String, FileInfo> indexed = new HashMap<>();

    private String currentDirectoryIndexing = "";

    private static final Logger LOG = Logger.getLogger(IndexStructure.class);

    public IndexStructure(DirectoryIndex directoryIndex) {
        this.directoryIndex = directoryIndex;
//        this.indexStorage = indexStorage;
//        this.indexRead = indexRead;
    }

    @Override
    public void run() {
        indexStorage = getIndexWrite();
        indexRead = getIndexRead();
//        indexStorage = getApp().getIndexForWrite();
//        indexRead = getApp().getIndexForRead();

        if (!indexStorage.isStorageReady() || !indexRead.isStorageReady()) {
            LOG.debug("Storage not ready");
            return;
        }

        ArrayList<FileInfo> indexedPaths = indexRead.getAllForIndex(directoryIndex.getName());
        for (FileInfo fi : indexedPaths) {
            indexed.put(fi.getUuid(), fi);
        }

        for (String path : directoryIndex.getPath()) {
            try {
                indexPath(Paths.get(path));

                for (String id : indexed.keySet()) {
                    indexStorage.deleteDocument(id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        directoryIndex.setLastStructureIndexed(LocalDateTime.now());
        DirectoryIndexStorage directoryIndexStorage = new DirectoryIndexStorage(getConfiguration());
        directoryIndexStorage.setDirectory(directoryIndex);
    }

    private void indexPath(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        proceedPath(dir, attrs);
                        currentDirectoryIndexing = dir.toAbsolutePath().toString();
//                        LOG.info("Index directory " + dir.toAbsolutePath().toString());

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        proceedPath(file, attrs);

                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        if (exc instanceof AccessDeniedException) {
                            return FileVisitResult.SKIP_SUBTREE;
                        }

                        return super.visitFileFailed(file, exc);
                    }
                });
            } catch (AccessDeniedException e) {
                // ignore
            }
        }
    }

    private void proceedPath(Path path, BasicFileAttributes attrs) {
//        Document doc = getDocumentFromPath(path, attrs);
//
//        String fpath = path.toAbsolutePath().toString();
//        String docUUID = UUID.nameUUIDFromBytes(fpath.getBytes()).toString().replace("-", "");

        FileInfo finfo = new FileInfo(path, attrs, directoryIndex);


        /*
        skip if extension in path exists in directory ignore extensions
         */
        if (directoryIndex.getIgnoreExtension().contains(finfo.getExt())) {
            return;
        }
        for (String pathToCheck : directoryIndex.getIgnorePath()) {
            if (finfo.getPath().indexOf(pathToCheck) == 0) {
                return;
            }
        }
        for (String patternToCheck : directoryIndex.getIgnorePhrase()) {
            if (finfo.getPath().indexOf(patternToCheck) != -1) {
                return;
            }
        }

        String docUUID = finfo.getUuid();

        FileInfo currentIndexFileInfo = indexed.getOrDefault(docUUID, null);
        if (currentIndexFileInfo != null) {
            finfo.setLastMetaIndexed(currentIndexFileInfo.getLastMetaIndexed());
            finfo.setContent(currentIndexFileInfo.getContent());
        }

        try {
            indexStorage.addDocument(finfo);
        } catch (IOException e) {
//            // ignore access error to file/dir
        }
        if (indexed.containsKey(docUUID)) {
            indexed.remove(docUUID);
        }
    }

//    private Document getDocumentFromPath(Path path, BasicFileAttributes attrs) {
//
//        Optional<String> opt = Optional.ofNullable(path.getFileName().toString())
//                .filter(f -> f.contains("."))
//                .map(f -> f.substring(path.getFileName().toString().lastIndexOf(".") + 1));
//
//        String ext = "";
//        if (opt.isPresent()) {
//            ext = opt.get();
//        }
//
//        Document doc = new Document();
//        String docUUID = UUID.nameUUIDFromBytes(path.toAbsolutePath().toString().getBytes()).toString().replace("-", "");
//        doc.add(new StringField("id", docUUID, Field.Store.YES));
//        doc.add(new TextField("path", path.toAbsolutePath().toString(), Field.Store.YES));
//
//        doc.add(new LongPoint("last_modified", attrs.lastModifiedTime().toMillis()));
//        doc.add(new StoredField("last_modified", attrs.lastModifiedTime().toMillis()));
//
//        doc.add(new LongPoint("indexed", System.currentTimeMillis()));
//        doc.add(new StoredField("indexed", System.currentTimeMillis()));
//
//        doc.add(new LongPoint("created", attrs.creationTime().toMillis()));
//        doc.add(new StoredField("created", attrs.creationTime().toMillis()));
//
//        doc.add(new LongPoint("size", attrs.size()));
//        doc.add(new StoredField("size", attrs.size()));
//
//        doc.add(new LongPoint("metaindexed", 0));
//        doc.add(new StoredField("metaindexed", 0));
//
//        doc.add(new TextField("name", path.getFileName().toString(), Field.Store.YES));
//        doc.add(new StringField("ext", ext, Field.Store.YES));
//        doc.add(new StringField("type", attrs.isDirectory() ? "d" : "f", Field.Store.YES));
//
//        doc.add(new StringField("indexname", directoryIndex.getName(), Field.Store.YES));
//
//
//        return doc;
//    }

    @Override
    public String getTaskName() {
        return "Indexing " + currentDirectoryIndexing;
    }


}
