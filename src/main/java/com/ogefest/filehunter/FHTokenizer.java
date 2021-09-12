package com.ogefest.filehunter;

import org.apache.lucene.analysis.util.CharTokenizer;

public class FHTokenizer extends CharTokenizer {

    @Override
    protected boolean isTokenChar(int ch) {

        char[] tokenChars = {'/', '\\', '.', ':', ',', '-', '_',' '};

        for (int i = 0; i < tokenChars.length; i++) {
            if (ch == tokenChars[i]) {
                return false;
            }
        }

        return true;
    }
}
