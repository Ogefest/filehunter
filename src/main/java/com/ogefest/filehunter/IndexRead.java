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
import java.io.ObjectInputFilter;
import java.nio.file.Paths;
import java.util.ArrayList;

public class IndexRead {

    private IndexReader reader;
    private IndexSearcher searcher;
    private Configuration conf;

    public IndexRead(Configuration conf) {
        this.conf = conf;
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(conf.getValue("storage.directory"))));
            searcher = new IndexSearcher(reader);
        } catch (IndexNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeIndex() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
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

    public SearchResult getByUuid(String uuid) {
        ArrayList<SearchResult> searchResults = query("id:" + uuid);

        if (searchResults.size() == 0) {
            return null;
        }

        return searchResults.get(0);
    }

    public ArrayList<SearchResult> query(String q) {

        ArrayList<SearchResult> result = new ArrayList<>();
        if (searcher == null) {
            return result;
        }
        try {
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser parser = new QueryParser("path", analyzer);

            Query query = parser.parse(q);
            TopDocs hits = searcher.search(query, 100);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);

                SearchResult sr = new SearchResult(doc.get("id"), doc.get("path"));
                result.add(sr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }


}
