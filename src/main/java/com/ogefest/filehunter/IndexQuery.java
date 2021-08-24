package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class IndexQuery {

    private IndexReader reader;
    private IndexSearcher searcher;

    public IndexQuery(String storagePath) throws IOException {
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(storagePath)));
            searcher = new IndexSearcher(reader);
        } catch (IndexNotFoundException e) {

        }
    }

    public ArrayList<String> getAllForIndex(String name) {

        ArrayList<String> result = new ArrayList<>();
        if (searcher == null) {
            return result;
        }

        try {
//            Query query = new MatchAllDocsQuery();
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("path", analyzer);
//            QueryParser parser = new QueryParser("path", analyzer);

            Query query = parser.parse("indexname:" + name);
//            Query query = parser.parse("ext:JPG");
//            Query query = new QueryParser("path", analyzer);
            TopDocs hits = searcher.search(query, Integer.MAX_VALUE);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                result.add(doc.get("path"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }



//    public ArrayList<Document> query(String q) {
//
//        Analyzer analyzer = new StandardAnalyzer();
//        QueryParser parser = new QueryParser(field, analyzer);
//
//        Query query = null;
//        try {
//            query = parser.parse(q);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        searcher.search(query, 50);
//    }


}
