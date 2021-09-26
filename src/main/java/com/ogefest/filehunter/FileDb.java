package com.ogefest.filehunter;

import org.apache.lucene.document.Document;

import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.sql.*;
import java.time.ZoneId;
import java.util.HashMap;

public class FileDb {

    private Configuration conf;
    private Connection conn;
    private String dbPath;
    private String connectionUrl;
    private HashMap<String, Integer> fileCached;

    public FileDb(Configuration conf) throws SQLException {
        this.conf = conf;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dbPath = conf.getValue("storage.directory") + "/files.db";
        connectionUrl = "jdbc:sqlite:" + dbPath;

        this.initializeDbIfNotExists();

        conn = DriverManager.getConnection(connectionUrl);
    }


    private void initializeDbIfNotExists() {

        File f = new File(dbPath);
        if (f.exists()) {
            return;
        }

        try (Connection conn = DriverManager.getConnection(connectionUrl)) {
            if (conn != null) {

                String sql = "CREATE TABLE IF NOT EXISTS files (\n"
                        + "	id integer PRIMARY KEY,\n"
                        + "	path text NOT NULL,\n"
                        + "	uuid text NOT NULL,\n"
                        + "	lastmod long NOT NULL,\n"
                        + " metaindexed long NOT NULL\n"
                        + ");";

                Statement stmt = conn.createStatement();
                stmt.executeQuery(sql);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void setFile(FileInfo file) {
        try {
            String sql = "SELECT id FROM files WHERE path = ? LIMIT 1";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, file.getPath());

            ResultSet rs  = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String updateSql = "UPDATE files SET lastmod = ? WHERE id = ?";
                PreparedStatement updateStatement = conn.prepareStatement(updateSql);

                updateStatement.setLong(1, file.getLastModified().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                updateStatement.setInt(2, id);

                updateStatement.executeUpdate();
            } else {
                String insertSql = "INSERT INTO files (path,lastmod,metaindexed,uuid) VALUES (?,?,?,?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertSql);

                insertStatement.setString(1, file.getPath());
                insertStatement.setLong(2, file.getLastModified().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                insertStatement.setInt(3, 0);
                insertStatement.setString(4, file.getUuid());

                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteDocument(String uuid) {

        String sql = "DELETE FROM files WHERE uuid = ?";
        try {
            PreparedStatement deleteStatement = conn.prepareStatement(sql);
            deleteStatement.setString(1, uuid);
            deleteStatement.executeUpdate();

        } catch (SQLException e) {

        }

    }


}
