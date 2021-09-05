package com.ogefest.filehunter.task;

import com.ogefest.filehunter.*;
import org.apache.lucene.document.*;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class IndexStructure extends Task {

    private IndexWrite indexStorage;
    private IndexRead indexRead;
    private Directory directory;

    private HashMap<String, String> fsStructure = new HashMap<>();
    private HashMap<String, String> indexed = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(IndexStructure.class);

    public IndexStructure(Directory directory) {
//        this.indexStorage = indexWriter;
//        this.indexRead = indexRead;
        this.directory = directory;

        if (indexRead != null) {
            ArrayList<String> indexedPaths = indexRead.getAllForIndex(directory.getName());
            for (String s : indexedPaths) {
                indexed.put(s, "1");
            }
        }
    }

    @Override
    public void run() {
        indexStorage = getApp().getIndexForWrite();
        indexRead = getApp().getIndexForRead();

        for (String path : directory.getPath()) {
            try {
                indexPath(Paths.get(path));

                for (String id : indexed.keySet()) {
                    indexStorage.deleteDocument(id);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        indexRead.closeIndex();
        indexStorage.closeIndex();
        directory.setLastStructureIndexed(LocalDateTime.now());
        DirectoryStorage directoryStorage = new DirectoryStorage(getApp().getConfiguration());
        directoryStorage.setDirectory(directory);

    }

    private void indexPath(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        proceedPath(dir, attrs);
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
        Document doc = getDocumentFromPath(path, attrs);

        String fpath = path.toAbsolutePath().toString();
        String docUUID = UUID.nameUUIDFromBytes(fpath.getBytes()).toString();

        try {
            indexStorage.addDocument(docUUID, doc);
        } catch (IOException e) {
            // ignore access error to file/dir
        }
        if (indexed.containsKey(docUUID)) {
            indexed.remove(docUUID);
        }
    }

    private Document getDocumentFromPath(Path path, BasicFileAttributes attrs) {

        Optional<String> opt = Optional.ofNullable(path.getFileName().toString())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(path.getFileName().toString().lastIndexOf(".") + 1));

        String ext = "";
        if (opt.isPresent()) {
            ext = opt.get();
        }

        Document doc = new Document();
        String docUUID = UUID.nameUUIDFromBytes(path.toAbsolutePath().toString().getBytes()).toString().replace("-", "");
        doc.add(new StringField("id", docUUID, Field.Store.YES));
        doc.add(new TextField("path", path.toAbsolutePath().toString(), Field.Store.YES));
        doc.add(new LongPoint("last_modified", attrs.lastModifiedTime().toMillis()));
        doc.add(new LongPoint("indexed", System.currentTimeMillis()));
        doc.add(new LongPoint("created", attrs.creationTime().toMillis()));
        doc.add(new TextField("name", path.getFileName().toString(), Field.Store.YES));
        doc.add(new StringField("ext", ext, Field.Store.YES));
        doc.add(new StringField("type", attrs.isDirectory() ? "d" : "f", Field.Store.YES));
        doc.add(new LongPoint("size", attrs.size()));
        doc.add(new StringField("indexname", directory.getName(), Field.Store.YES));

        return doc;
    }


}
