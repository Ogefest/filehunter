package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.*;
import com.ogefest.filehunter.index.DirectoryIndex;
import com.ogefest.filehunter.index.DirectoryIndexStorage;
import com.ogefest.filehunter.task.Worker;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;

public class Lucene implements FileSystemDatabase {

    private static final Logger LOG = Logger.getLogger(Worker.class);
    private IndexWriter writer;
    private IndexReader reader;
    private IndexSearcher searcher;
    private Configuration conf;
    private long sessionId = 0;
    private DirectoryIndexStorage indexStorage;
    private int docAddCounter = 0;

    public Lucene(Configuration conf) {
        this.conf = conf;
        indexStorage = new DirectoryIndexStorage(conf);
    }

    private String getUuidByPath(String path, DirectoryIndex index) {
        String k = path + index.getName();
        String docUUID = UUID.nameUUIDFromBytes(k.getBytes()).toString().replace("-", "");

        return docUUID;
    }

    private String getParentUuidByPath(String path, DirectoryIndex index) {
        String[] elems = path.split("/");
        String parentPath = "";
        if (elems.length > 2) {
            for (int i = 0; i < elems.length - 1; i++) {
                if (elems[i].length() > 0) {
                    parentPath = parentPath + "/" + elems[i];
                }
            }
        } else {
            parentPath = "/";
        }

        return getUuidByPath(parentPath, index);
    }

    @Override
    public void clear(FileInfo fi) {
        Analyzer analyzer = new FHAnalyzer();//FHAnalyzer.get();
        QueryParser parser = new QueryParser("ident", analyzer);

        String queryToCleanup = "ident:" + fi.getId() + " AND indexname:" + fi.getIndexName();

        Query query = null;
        try {
            query = parser.parse(queryToCleanup);
            writer.deleteDocuments(query);
            writer.commit();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileInfo add(String path, FileAttributes attributes, DirectoryIndex index) {

        String docUUID = getUuidByPath(path, index);
        String parentDocUUID = getParentUuidByPath(path, index);

        FileInfo fi = new FileInfo(docUUID, parentDocUUID, path, index.getName(), attributes);

        setDocument(fi);

        return fi;
    }

    @Override
    public FileInfo add(FileInfo doc) {
        setDocument(doc);
        return doc;
    }

    private void setDocument(FileInfo fi) {

//        docAddCounter++;
//        if (docAddCounter % 1000 == 0) {
//            LOG.debug("Clean up directory with deleteUnusedFiles");
//            try {
//
//                writer.commit();
////                writer.close();
//                writer.forceMergeDeletes();
//                writer.forceMerge(1);
//                writer.commit();
//                writer.deleteUnusedFiles();
//                writer.commit();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        boolean extractMetadataInFileIndex = false;
        if (indexStorage.getByName(fi.getIndexName()).isExtractMetadata()) {
            extractMetadataInFileIndex = true;
        }

        Document doc = new Document();

        doc.add(new StringField("ident", fi.getId(), Field.Store.YES));
        doc.add(new StringField("parent", fi.getParentId(), Field.Store.YES));
        doc.add(new TextField("path", fi.getPath(), Field.Store.YES));

        doc.add(new LongPoint("last_modified", fi.getFileAttributes().getLastModified().toEpochSecond(ZoneOffset.UTC) * 1000));
        doc.add(new StoredField("last_modified", fi.getFileAttributes().getLastModified().toEpochSecond(ZoneOffset.UTC) * 1000));

        doc.add(new StringField("sessionid", String.valueOf(sessionId), Field.Store.YES));

        doc.add(new LongPoint("size", fi.getSize()));
        doc.add(new StoredField("size", fi.getSize()));

        doc.add(new LongPoint("metaindexed", fi.getFileAttributes().getLastMetaIndexed().toEpochSecond(ZoneOffset.UTC) * 1000));
        doc.add(new StoredField("metaindexed", fi.getFileAttributes().getLastMetaIndexed().toEpochSecond(ZoneOffset.UTC) * 1000));

        if (extractMetadataInFileIndex) {
            if (fi.getLastMetaIndexed().isBefore(fi.getLastModified())) {
                doc.add(new StringField("tometareindex", "t", Field.Store.YES));
            } else {
                doc.add(new StringField("tometareindex", "f", Field.Store.YES));
            }
        } else {
            doc.add(new StringField("tometareindex", "t", Field.Store.YES));
        }

        doc.add(new TextField("name", fi.getName(), Field.Store.YES));
        doc.add(new StringField("ext", fi.getExt(), Field.Store.YES));
        doc.add(new StringField("type", fi.getFileAttributes().getType() == FileType.DIRECTORY ? "d" : "f", Field.Store.YES));

        doc.add(new StringField("indexname", fi.getIndexName(), Field.Store.YES));
        doc.add(new TextField("content", fi.getFileAttributes().getContent(), Field.Store.YES));

        LOG.debug("Doc add " + fi.getPath());
        try {

            Analyzer analyzer = new FHAnalyzer();
            QueryParser parser = new QueryParser("ident", analyzer);
            String queryToCleanup = "ident:" + fi.getId() + " AND indexname:" + fi.getIndexName();
            Query query = parser.parse(queryToCleanup);
            writer.deleteDocuments(query);
            writer.addDocument(doc);

            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<FileInfoLucene> searchForDocuments(String q) {

        ArrayList<FileInfoLucene> result = new ArrayList<>();

        try {
            DirectoryReader directoryReader;
            if (writer != null && writer.isOpen()) {
                directoryReader = DirectoryReader.open(writer, true, true);
            } else {
                directoryReader = DirectoryReader.open(FSDirectory.open(Paths.get(conf.getValue("storage.directory"))));
            }

            searcher = new IndexSearcher(directoryReader);

            Analyzer analyzer = new FHAnalyzer();//FHAnalyzer.get();
            QueryParser parser = new QueryParser("ident", analyzer);

            Query query = parser.parse(q);
            TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                result.add(new FileInfoLucene(doc));
            }


            directoryReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public FileInfo get(String path, DirectoryIndex index) {
        String uuid = getUuidByPath(path, index);

        return get(uuid);
    }

    @Override
    public FileInfo get(String uid) {

        String q = "ident:" + uid;

        ArrayList<FileInfoLucene> searchResult = searchForDocuments(q);
        if (searchResult.size() == 1) {
            return searchResult.get(0).getFileInfo();
        }

        return null;
    }

    @Override
    public ArrayList<FileInfo> list(String path, DirectoryIndex index) {
        String uuid = getUuidByPath(path, index);

        return list(uuid);
    }

    @Override
    public ArrayList<FileInfo> list(String uid) {
        ArrayList<FileInfo> result = new ArrayList<>();

        String q = "parent:" + uid;
        ArrayList<FileInfoLucene> tmp = searchForDocuments(q);
        for (FileInfoLucene fil : tmp) {
            result.add(fil.getFileInfo());
        }

        return result;
    }

    @Override
    public boolean exists(FileInfo fi) {
        return get(fi.getId()) != null;
    }

    @Override
    public boolean exists(String path, DirectoryIndex index) {
        String uuid = getUuidByPath(path, index);

        return get(uuid) != null;
    }

    @Override
    public void setCurrentStatus(FileInfo fi, int counter) {


    }

    @Override
    public void setCurrentAttributes(FileInfo fi, FileAttributes attributes) {
        fi.setFileAttributes(attributes);
        setDocument(fi);
    }

    @Override
    public void openReindexingSession(int sessionId, DirectoryIndex index) {
        this.sessionId = sessionId;
        indexStorage = new DirectoryIndexStorage(conf);

        LOG.info("Reindex session opened " + index.getName());

        try {

            Analyzer analyzer = new FHAnalyzer();
            FSDirectory storage = FSDirectory.open(Paths.get(conf.getValue("storage.directory")));


            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            iwc.setMaxBufferedDocs(1000);
            iwc.setRAMBufferSizeMB(1024);
//            iwc.setMaxBufferedDocs(10);
//            iwc.setRAMBufferSizeMB(5);


            writer = new IndexWriter(storage, iwc);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeReindexingSession(int sessionId, DirectoryIndex index) {

        Analyzer analyzer = new FHAnalyzer();//FHAnalyzer.get();
        QueryParser parser = new QueryParser("ident", analyzer);

        String queryToCleanup = "!sessionid:" + sessionId + " AND indexname:" + index.getName();

        Query query = null;
        try {
            query = parser.parse(queryToCleanup);
            writer.commit();
            writer.deleteDocuments(query);

            writer.commit();
            writer.deleteUnusedFiles();
            writer.forceMerge(1);
            writer.close();

        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        closeConnection();
        LOG.info("Reindex session closed " + index.getName());
    }

    @Override
    public void closeConnection() {
        try {
            if (writer.isOpen()) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
