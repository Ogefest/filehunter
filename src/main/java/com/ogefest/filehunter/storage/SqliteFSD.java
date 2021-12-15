package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.DirectoryIndex;
import com.ogefest.filehunter.FileAttributes;
import com.ogefest.filehunter.FileInfo;

import java.sql.*;

public class SqliteFSD implements FileSystemDatabase {

    private Configuration conf;
    private Connection conn;

    public SqliteFSD(Configuration conf) {
        this.conf = conf;

        String connectionUrl = conf.getValue("DB_URL");
        if (connectionUrl == null || connectionUrl.equals("")) {
            connectionUrl = "jdbc:sqlite:/tmp/filehunter.fs.db";
        }

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(connectionUrl);

            String sql = "CREATE TABLE IF NOT EXISTS filesystem (\n" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
//                    "parent_id INTEGER NOT NULL,\n" +
                    "fname TEXT NOT NULL,\n" +
                    "storage TEXT NOT NULL,\n" +
                    "fsize INTEGER,\n" +
                    "hash TEXT,\n" +
                    "reindex_counter INTEGER DEFAULT 0\n" +
//                    "CONSTRAINT filesystem_PK PRIMARY KEY (id)\n" +
                    ")";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

            stmt = conn.createStatement();
            String indexSql1 = "CREATE INDEX IF NOT EXISTS filesystem_crc32_IDX ON filesystem (hash);";
            stmt.execute(indexSql1);

            stmt = conn.createStatement();
            String indexSql2 = "CREATE INDEX IF NOT EXISTS filesystem_path_IDX ON filesystem (fname,storage)";
            stmt.execute(indexSql2);

//            stmt = conn.createStatement();
//            String indexSql3 = "CREATE INDEX IF NOT EXISTS filesystem_parent_IDX ON filesystem (parent_id)";
//            stmt.execute(indexSql3);


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (conn != null) {
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                System.out.println(ex.getMessage());
//            }
        }
    }

    @Override
    public void add(FileInfo fi) {

        String sql = "INSERT INTO filesystem (fname, storage, hash, fsize) VALUES(?,?,?,?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, fi.getPath());
            pstmt.setString(2, fi.getIndex().getName());
            pstmt.setString(3, fi.getHash());
            pstmt.setInt(4, 0);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clear(FileInfo fi) {
        String sql = "DELETE FROM filesystem WHERE hash = ? AND path = ?";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fi.getHash());
            pstmt.setString(2, fi.getPath());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public FileInfo get(String path, DirectoryIndex index) {
        FileInfo fi = new FileInfo(path, index, new FileAttributes());

        if (exists(fi)) {
            return fi;
        }

        return null;
    }

    @Override
    public boolean exists(FileInfo fi) {
        String sql = "SELECT id FROM filesystem WHERE hash = ? AND path = ?";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fi.getHash());
            pstmt.setString(2, fi.getPath());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void setReindexCounter(FileInfo fi, int counter) {
        String sql = "UPDATE filesystem SET reindex_counter = ? WHERE hash = ? AND path = ?";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, counter);
            pstmt.setString(2, fi.getHash());
            pstmt.setString(3, fi.getPath());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
