package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;

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
