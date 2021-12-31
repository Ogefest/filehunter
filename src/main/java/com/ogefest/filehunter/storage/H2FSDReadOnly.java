package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.Configuration;

import java.sql.SQLException;

public class H2FSDReadOnly extends H2FSD {

    protected boolean isInMemory = false;

    public H2FSDReadOnly(Configuration conf) {
        super(conf);
    }

    @Override
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
