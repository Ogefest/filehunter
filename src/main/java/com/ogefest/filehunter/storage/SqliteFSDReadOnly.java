package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.Configuration;

import java.sql.SQLException;

public class SqliteFSDReadOnly extends SqliteFSD {

    protected boolean isInMemory = false;

    public SqliteFSDReadOnly(Configuration conf) {
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
