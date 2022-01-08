package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.FHAnalyzer;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.FileInfoLucene;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LuceneSearch {

    private IndexReader reader;
    private IndexSearcher searcher;
    private Configuration conf;

    public LuceneSearch(Configuration conf) {
        this.conf = conf;
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(conf.getValue("storage.directory"))));
            searcher = new IndexSearcher(reader);
        } catch (IndexNotFoundException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeIndex() {
        if (reader == null) {
            return;
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<FileInfo> queryByRawQuery(String q, int resultCount) {
        ArrayList<FileInfo> result = new ArrayList<>();
        if (searcher == null) {
            return result;
        }
//        Analyzer analyzer = new StandardAnalyzer();

        Analyzer analyzer = new FHAnalyzer();//FHAnalyzer.get();
        QueryParser parser = new QueryParser("ident", analyzer);

//        QueryParser parser = new QueryParser("ident", analyzer);
//        parser.setDefaultOperator(QueryParser.Operator.AND);
        Query query = null;
        try {
            query = parser.parse(q);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TopDocs hits = null;
        try {
            hits = searcher.search(query, resultCount);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);

                FileInfoLucene finfo = new FileInfoLucene(doc);
                FileInfo fi = new FileInfo(finfo);
//                FileItem fi = new FileItem(finfo);
                result.add(fi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<FileInfo> query(String q) {
        return query(q, new HashMap<>());
    }

    public ArrayList<FileInfo> query(String q, HashMap<String, String> filters) {

        ArrayList<FileInfo> result = new ArrayList<>();
        if (searcher == null) {
            return result;
        }
        try {
            Analyzer analyzer = new StandardAnalyzer();

            QueryParser parser = new QueryParser("path", analyzer);
            parser.setDefaultOperator(QueryParser.Operator.AND);
            Query pathQueryTmp = parser.parse(q);
            BoostQuery pathQuery = new BoostQuery(pathQueryTmp, 1.2f);

            QueryParser parserContent = new QueryParser("content", analyzer);
            parserContent.setDefaultOperator(QueryParser.Operator.AND);
            Query contentQuery = parserContent.parse(q);


            // path^1.2 OR content
            BooleanQuery.Builder mainQuery = new BooleanQuery.Builder();
            mainQuery.add(pathQuery, BooleanClause.Occur.SHOULD);
            mainQuery.add(contentQuery, BooleanClause.Occur.SHOULD);


            // final + filters
            BooleanQuery.Builder finalQuery = new BooleanQuery.Builder();
            finalQuery.add(mainQuery.build(), BooleanClause.Occur.MUST);
            addSingleFilters(finalQuery, filters);
            addRangeFilters(finalQuery, filters);


            Query searchQuery = finalQuery.build();

            TopDocs hits = searcher.search(searchQuery, 100);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);

                FileInfoLucene finfo = new FileInfoLucene(doc);
                FileInfo fi = new FileInfo(finfo);
//                FileItem fi = new FileItem(finfo);
                result.add(fi);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void addSingleFilters(BooleanQuery.Builder query, HashMap<String, String> queryFilters) {

        String supportedFields[] = {"indexname", "ext", "name", "content", "path"};
        List<String> supportedFilters = Arrays.asList(supportedFields);

        for (String k : supportedFilters) {
            if (queryFilters.containsKey(k)) {
                query.add(new TermQuery(new Term(k, queryFilters.get(k))), BooleanClause.Occur.MUST);
            }
        }
    }

    private void addRangeFilters(BooleanQuery.Builder query, HashMap<String, String> queryFilters) {
        HashMap<String, List<String>> supported = new HashMap<>();

        String sizeKeys[] = {"minsize", "maxsize"};
        supported.put("size", Arrays.asList(sizeKeys));

        String modifiedKeys[] = {"modified_after", "modified_before"};
        supported.put("last_modified", Arrays.asList(modifiedKeys));

        String createdKeys[] = {"created_after", "created_before"};
        supported.put("created", Arrays.asList(createdKeys));

        for (String k : supported.keySet()) {

            String fieldMin = supported.get(k).get(0);
            String fieldMax = supported.get(k).get(1);

            if (!queryFilters.containsKey(fieldMin) && !queryFilters.containsKey(fieldMax)) {
                continue;
            }
            long fieldMinLong = 0;
            if (queryFilters.containsKey(fieldMin)) {
                fieldMinLong = Long.parseLong(queryFilters.get(fieldMin));
            }

            long fieldMaxLong = Long.MAX_VALUE;
            if (queryFilters.containsKey(fieldMax) && !queryFilters.get(fieldMax).equals("0")) {
                fieldMaxLong = Long.parseLong(queryFilters.get(fieldMax));
            }

            query.add(LongPoint.newRangeQuery(k, fieldMinLong, fieldMaxLong), BooleanClause.Occur.MUST);
        }
    }

    public int getNumDocs() {
        if (reader == null) {
            return 0;
        }
        return reader.numDocs();
    }


}
