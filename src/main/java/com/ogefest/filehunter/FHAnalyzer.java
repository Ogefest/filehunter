package com.ogefest.filehunter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;

public class FHAnalyzer extends Analyzer  {
    @Override
    protected TokenStreamComponents createComponents(String s) {
        Tokenizer tokenizer = new FHTokenizer();
        TokenStream filter = new LowerCaseFilter(tokenizer);
        filter = new TrimFilter(filter);

        return new TokenStreamComponents(tokenizer, filter);
    }
}
