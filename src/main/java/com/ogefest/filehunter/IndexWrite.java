package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class IndexWrite {

    IndexWriter writer;
    long indexCounter = 0;

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

    public void addDocument(String id, Document doc) throws IOException {
        writer.updateDocument(new Term("id", id), doc);


        List<String> res = analyze(doc.get("path"), FHAnalyzer.get());

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

}
