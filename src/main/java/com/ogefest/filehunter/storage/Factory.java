package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.Configuration;

public class Factory {
    private static FileSystemDatabase db = null;

    public static FileSystemDatabase get(Configuration conf) {
        if (db == null) {
            db = new Lucene(conf);
        }
        return db;
    }
}
