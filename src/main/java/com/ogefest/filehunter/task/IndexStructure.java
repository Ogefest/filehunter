package com.ogefest.filehunter.task;

import com.ogefest.filehunter.Directory;
import com.ogefest.filehunter.IndexQuery;
import com.ogefest.filehunter.IndexStorage;
import org.apache.lucene.document.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class IndexStructure implements Task {

    private IndexStorage indexStorage;
    private IndexQuery indexQuery;
    private Directory directory;

    private HashMap<String, String> fsStructure = new HashMap<>();
    private HashMap<String, String> indexed = new HashMap<>();

    public IndexStructure(Directory directory, IndexStorage indexWriter, IndexQuery indexQuery) {
        this.indexStorage = indexWriter;
        this.indexQuery = indexQuery;
        this.directory = directory;

        if (indexQuery != null) {
            ArrayList<String> indexedPaths = indexQuery.getAllForIndex(directory.getName());
            for (String s : indexedPaths) {
                indexed.put(s, "1");
            }
        }
    }

    @Override
    public void run() {
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
    }

    private void indexPath(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try {
                            Document doc = getDocumentFromPath(file, attrs);

                            String fpath = file.toAbsolutePath().toString();
                            String docUUID = UUID.nameUUIDFromBytes(fpath.getBytes()).toString();

                            indexStorage.addDocument(docUUID, doc);
                            if (indexed.containsKey(docUUID)) {
                                indexed.remove(docUUID);
                            }
                        } catch (IOException ignore) {
                            // don't index files that can't be read.
                        }
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

    private Document getDocumentFromPath(Path path, BasicFileAttributes attrs) {

        Optional<String> opt = Optional.ofNullable(path.getFileName().toString())
                .filter(f -> f.contains("."))
                .map(f -> f.substring(path.getFileName().toString().lastIndexOf(".") + 1));

        String ext = "";
        if (opt.isPresent()) {
            ext = opt.get();
        }

        Document doc = new Document();
        String docUUID = UUID.nameUUIDFromBytes(path.toAbsolutePath().toString().getBytes()).toString();
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
