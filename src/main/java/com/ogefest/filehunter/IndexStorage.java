package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class IndexStorage {

    IndexWriter writer;
    long indexCounter = 0;

    public IndexStorage(String storagePath) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        FSDirectory storage = FSDirectory.open(Paths.get(storagePath));

        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        writer = new IndexWriter(storage, iwc);
    }

    public void finish() {
        try {
            writer.commit();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDocument(String id, Document doc) throws IOException {
        writer.updateDocument(new Term("id", id), doc);
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

            indexCounter++;
            if (indexCounter % 1000 == 0) {
                writer.commit();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
