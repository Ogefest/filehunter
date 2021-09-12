package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.path.PathHierarchyTokenizerFactory;
import org.apache.lucene.analysis.standard.*;
//import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.IOException;
import java.nio.file.Paths;

public class FHAnalyzer  {

    public static Analyzer get() {

        return new FHAnalyzer2();

//        try {
//
//            Analyzer analyzer = CustomAnalyzer.builder()
////                    .withTokenizer()
////                    .addTokenFilter("pathHierarchy")
////                    .add
////                    .withTokenizer(new FHTokenizer())
//                    .addTokenFilter("lowercase")
////                    .addTokenFilter("stop")
//                    .addTokenFilter("trim")
////                    .addTokenFilter("porterstem")
////                    .addTokenFilter("capitalization")
//                    .build();
//
//            return analyzer;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;

    }
}
