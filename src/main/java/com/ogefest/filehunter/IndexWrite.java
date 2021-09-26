package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IndexWrite {

    private IndexWriter writer;
    private long indexCounter = 0;
    private Configuration conf;

    public IndexWrite(Configuration conf) {

        this.conf = conf;

        try {

            Analyzer analyzer2 = new StandardAnalyzer();
            Analyzer analyzer = FHAnalyzer.get();
            FSDirectory storage = FSDirectory.open(Paths.get(conf.getValue("storage.directory")));

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            writer = new IndexWriter(storage, iwc);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStorageReady() {
        return writer != null;
    }

    public void closeIndex() {
        if (writer == null) {
            return;
        }
        try {
            writer.commit();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDocument(FileInfo fileInfo) throws IOException {
        Document doc = getDocumentFromFileInfo(fileInfo);

        if (fileInfo.getLastModified().isAfter(fileInfo.getLastMetaIndexed())) {
            doc.removeField("tometareindex");
            doc.add(new IntPoint("tometareindex", 1));
        }

        writer.updateDocument(new Term("id", fileInfo.getUuid()), doc);

        indexCounter++;
        if (indexCounter % 1000 == 0) {
            writer.commit();
        }
    }

    public void deleteDocs(ArrayList<String> ids) {
        for (String id : ids) {
            deleteDocument(id);
        }
    }

    public void deleteDocument(String id) {
        try {
            writer.deleteDocuments(new Term("id", id));
//            fileDb.deleteDocument(id);

            indexCounter++;
            if (indexCounter % 1000 == 0) {
                writer.commit();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDocumentByDirectoryName(String name) {
        try {
            writer.deleteDocuments(new Term("indexname", name));
            writer.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> analyze(String text, Analyzer analyzer) throws IOException{
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("path", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

    private Document getDocumentFromFileInfo(FileInfo fileInfo) {

        Document doc = new Document();
//        String docUUID = UUID.nameUUIDFromBytes(path.toAbsolutePath().toString().getBytes()).toString().replace("-", "");

        doc.add(new StringField("id", fileInfo.getUuid(), Field.Store.YES));
        doc.add(new TextField("path", fileInfo.getPath(), Field.Store.YES));

        doc.add(new LongPoint("last_modified", fileInfo.getLastModified().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));
        doc.add(new StoredField("last_modified", fileInfo.getLastModified().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));

        doc.add(new LongPoint("created", fileInfo.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));
        doc.add(new StoredField("created", fileInfo.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));

        doc.add(new LongPoint("size", fileInfo.getSize() ));
        doc.add(new StoredField("size", fileInfo.getSize() ));

        doc.add(new LongPoint("metaindexed", fileInfo.getLastMetaIndexed().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        doc.add(new StoredField("metaindexed", fileInfo.getLastMetaIndexed().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        doc.add(new IntPoint("tometareindex", 0));

        doc.add(new TextField("name", fileInfo.getName(), Field.Store.YES));
        doc.add(new StringField("ext", fileInfo.getExt(), Field.Store.YES));
        doc.add(new StringField("type", fileInfo.getType() == FileType.DIRECTORY ? "d" : "f", Field.Store.YES));

        doc.add(new StringField("indexname", fileInfo.getIndexname(), Field.Store.YES));

        doc.add(new TextField("content", fileInfo.getContent(), Field.Store.YES));

        return doc;
    }

}
