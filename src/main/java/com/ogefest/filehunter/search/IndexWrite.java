package com.ogefest.filehunter.search;

import com.ogefest.filehunter.*;
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class IndexWrite {

    private IndexWriter writer;
    private long indexCounter = 0;
    private Configuration conf;

    public IndexWrite(Configuration conf) {

        this.conf = conf;

        try {

            Analyzer analyzer2 = new StandardAnalyzer();
            Analyzer analyzer = new FHAnalyzer();// FHAnalyzer.get();
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

    public void addDocument(FileInfoLucene fileInfoLucene) throws IOException {
        Document doc = getDocumentFromFileInfo(fileInfoLucene);

        if (fileInfoLucene.getLastModified().isAfter(fileInfoLucene.getLastMetaIndexed())) {
            doc.removeField("tometareindex");
            doc.add(new IntPoint("tometareindex", 1));
        }

        writer.updateDocument(new Term("id", fileInfoLucene.getUuid()), doc);

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

    private Document getDocumentFromFileInfo(FileInfoLucene fileInfoLucene) {

        Document doc = new Document();
//        String docUUID = UUID.nameUUIDFromBytes(path.toAbsolutePath().toString().getBytes()).toString().replace("-", "");

        doc.add(new StringField("id", fileInfoLucene.getUuid(), Field.Store.YES));
        doc.add(new TextField("path", fileInfoLucene.getPath(), Field.Store.YES));

        doc.add(new LongPoint("last_modified", fileInfoLucene.getLastModified().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));
        doc.add(new StoredField("last_modified", fileInfoLucene.getLastModified().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));

        doc.add(new LongPoint("created", fileInfoLucene.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));
        doc.add(new StoredField("created", fileInfoLucene.getCreated().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() ));

        doc.add(new LongPoint("size", fileInfoLucene.getSize() ));
        doc.add(new StoredField("size", fileInfoLucene.getSize() ));

        doc.add(new LongPoint("metaindexed", fileInfoLucene.getLastMetaIndexed().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        doc.add(new StoredField("metaindexed", fileInfoLucene.getLastMetaIndexed().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        doc.add(new IntPoint("tometareindex", 0));

        doc.add(new TextField("name", fileInfoLucene.getName(), Field.Store.YES));
        doc.add(new StringField("ext", fileInfoLucene.getExt(), Field.Store.YES));
        doc.add(new StringField("type", fileInfoLucene.getType() == FileType.DIRECTORY ? "d" : "f", Field.Store.YES));

        doc.add(new StringField("indexname", fileInfoLucene.getIndexname(), Field.Store.YES));

        doc.add(new TextField("content", fileInfoLucene.getContent(), Field.Store.YES));

        return doc;
    }

}
