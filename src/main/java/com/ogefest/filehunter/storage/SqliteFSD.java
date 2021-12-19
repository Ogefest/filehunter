package com.ogefest.filehunter.storage;

import com.ogefest.filehunter.*;

import java.io.File;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;

public class SqliteFSD implements FileSystemDatabase {

    protected Configuration conf;
    protected Connection conn;
    protected String dbFilePath = null;
    protected boolean isInMemory = true;

    public SqliteFSD(Configuration conf) {
        this.conf = conf;

        dbFilePath = conf.getValue("storage.directory") + "/filesystem.db";

        String connectionUrl = conf.getValue("DB_URL");
        if (connectionUrl == null || connectionUrl.equals("")) {
            if (isInMemory) {
                connectionUrl = "jdbc:sqlite::memory:";
            } else {
                connectionUrl = "jdbc:sqlite:" + dbFilePath;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(connectionUrl);
            File f = new File(dbFilePath);
            f = new File(dbFilePath);
            if (isInMemory && f.exists()) {
                conn.createStatement().executeUpdate("restore from " + dbFilePath);
            }

            String sql = "CREATE TABLE IF NOT EXISTS filesystem (\n" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "parent_id INTEGER NOT NULL,\n" +
                    "fname TEXT NOT NULL,\n" +
                    "fpath TEXT,\n" +
                    "storage TEXT NOT NULL,\n" +
                    "fsize INTEGER,\n" +
                    "ftype TEXT,\n" +
                    "flast_modified INTEGER,\n" +
                    "fts_status INTEGER DEFAULT 0,\n" +
                    "is_visible INTEGER DEFAULT 1,\n" +
                    "fattributes TEXT,\n" +
                    "reindex_counter INTEGER DEFAULT 0\n" +
//                    "CONSTRAINT filesystem_PK PRIMARY KEY (id)\n" +
                    ")";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

            stmt = conn.createStatement();
            String indexSql1 = "CREATE INDEX IF NOT EXISTS filesystem_fts_status_IDX ON filesystem (fts_status);";
            stmt.execute(indexSql1);

            stmt = conn.createStatement();
            String indexSql2 = "CREATE INDEX IF NOT EXISTS filesystem_path_IDX ON filesystem (fname,storage)";
            stmt.execute(indexSql2);

            stmt = conn.createStatement();
            String indexSql3 = "CREATE INDEX IF NOT EXISTS filesystem_parent_IDX ON filesystem (parent_id)";
            stmt.execute(indexSql3);

            stmt = conn.createStatement();
            String indexSql4 = "CREATE INDEX IF NOT EXISTS filesystem_fpath_IDX ON filesystem (fpath)";
            stmt.execute(indexSql4);

            String sql2 = "CREATE TABLE IF NOT EXISTS reindexsync (\n" +
                    "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "ident INTEGER NOT NULL,\n" +
                    "indexname TEXT NOT NULL,\n" +
                    "is_finished INTEGER DEFAULT 0,\n" +
                    "added INTEGER NOT NULL\n" +
                    ")";
            stmt = conn.createStatement();
            stmt.execute(sql2);


//            String sql3 = "PRAGMA journal_mode=WAL";
//            stmt = conn.createStatement();
//            stmt.execute(sql3);


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileInfo add(String path, FileAttributes attributes, DirectoryIndex index) {

        String sql = "INSERT INTO filesystem (fname, storage, parent_id, fsize, ftype, flast_modified,fpath) VALUES(?,?,?,?,?,?,?)";
        String[] pathElems = path.split("/");

        int parentId = getParentIdByPath(path, index);

        String pathName = pathElems[pathElems.length - 1];
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, pathName);
            pstmt.setString(2, index.getName());
            pstmt.setInt(3, parentId);
            pstmt.setLong(4, attributes.getSize());
            pstmt.setString(5, attributes.getType() == FileType.FILE ? "f" : "d");
            pstmt.setLong(6, attributes.getLastModified().toEpochSecond(ZoneOffset.UTC));
            pstmt.setString(7, path);

            pstmt.executeUpdate();

            int insertedId = pstmt.getGeneratedKeys().getInt(1);
            return getById(insertedId);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void clear(FileInfo fi) {
        String sql = "DELETE FROM filesystem WHERE id = ?";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fi.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public FileInfo get(String path, DirectoryIndex index) {

        int id = getIdByPath(path, index);
        if (id == 0) {
            int parentId = getParentIdByPath(path, index);

            FileInfo result = new FileInfo(0, parentId, path, index.getName(), new FileAttributes());
            return result;
        }

        return getById(id);
    }

    @Override
    public ArrayList<FileInfo> list(String path, DirectoryIndex index) {
        int id = getIdByPath(path, index);

        ArrayList<FileInfo> result = new ArrayList<>();

        if (id == 0 && !path.equals("/")) {
            return result;
        }

        String sql = "SELECT id FROM filesystem WHERE parent_id = ? AND storage = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, index.getName());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                result.add(getById(rs.getInt("id")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean exists(FileInfo fi) {
        String sql = "SELECT id FROM filesystem WHERE id = ?";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fi.getId());
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
    public boolean exists(String path, DirectoryIndex index) {
        int itemId = getIdByPath(path, index);
        if (itemId > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void setCurrentStatus(FileInfo fi, int counter) {
        String sql = "UPDATE filesystem SET reindex_counter = ? WHERE id = ?";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, counter);
            pstmt.setInt(2, fi.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentFTSStatus(FileInfo fi, int ftsStatus) {
        String sql = "UPDATE filesystem SET fts_status = ? WHERE id = ?";

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ftsStatus);
            pstmt.setInt(2, fi.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentAttributes(FileInfo fi, FileAttributes attributes) {

        String sql = "UPDATE filesystem SET fsize = ?, flast_modified = ? WHERE id = ?";

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setLong(1, attributes.getSize());
            preparedStatement.setLong(2, attributes.getLastModified().toEpochSecond(ZoneOffset.UTC));
            preparedStatement.setInt(3, fi.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ArrayList<FileInfo> getItemsToClear() {
        ArrayList<FileInfo> result = new ArrayList<>();

        String sql = "SELECT id FROM filesystem WHERE fts_status = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, FTSStatus.TO_REMOVE.getValue());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(getById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public ArrayList<FileInfo> getItemsToFullTextIndex() {
        ArrayList<FileInfo> result = new ArrayList<>();

        String sql = "SELECT id FROM filesystem WHERE fts_status = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, FTSStatus.TO_ADD.getValue());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                result.add(getById(rs.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void openReindexingSession(int sessionId, DirectoryIndex index) {

        String sql = "INSERT INTO reindexsync (ident, added, indexname) VALUES (?,?,?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sessionId);
            pstmt.setInt(2, (int) Instant.now().getEpochSecond());
            pstmt.setString(3, index.getName());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeReindexingSession(int sessionId, DirectoryIndex index) {

        String sql = "UPDATE reindexsync SET is_finished = ? WHERE ident = ? AND indexname = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, sessionId);
            preparedStatement.setString(3, index.getName());
            preparedStatement.executeUpdate();

            String sql2 = "SELECT id,ident FROM reindexsync WHERE ident != ? AND indexname = ?";
            preparedStatement = conn.prepareStatement(sql2);
            preparedStatement.setInt(1, sessionId);
            preparedStatement.setString(2, index.getName());

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String sql3 = "UPDATE filesystem SET fts_status = ?, is_visible=0 WHERE reindex_counter = ? AND storage = ?";
                preparedStatement = conn.prepareStatement(sql3);
                preparedStatement.setInt(1, FTSStatus.TO_REMOVE.getValue());
                preparedStatement.setInt(2, rs.getInt("ident"));
                preparedStatement.setString(3, index.getName());
                preparedStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void closeConnection() {
        try {
            if (isInMemory) {
                conn.createStatement().executeUpdate("backup to " + dbFilePath);
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected String getPathById(int id) {
        String result = "";

        String sql = "SELECT fpath FROM filesystem WHERE id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("fpath");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;

    }

    protected int getParentIdByPath(String path, DirectoryIndex index) {
        String[] pathElems = path.split("/");
        if (pathElems.length <= 2) {
            return 0;
        }
        String[] parentPathElems = Arrays.copyOf(pathElems, pathElems.length - 1);
        String result = "";

        for (String pelem : parentPathElems) {
            if (pelem.length() > 0) {
                result += "/" + pelem;
            }
        }

        return getIdByPath(result, index);
    }

    protected int getIdByPath(String path, DirectoryIndex index) {


        String sql = "SELECT id FROM filesystem WHERE fpath = ? AND storage = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, path);
            pstmt.setString(2, index.getName());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;

//        String[] pathElems = path.substring(1).split("/");
//        int parentId = 0;
//        String sql = "SELECT id FROM filesystem WHERE fname = ? AND parent_id = ? AND storage = ?";
//        for (String elem : pathElems) {
//            try {
//                PreparedStatement pstmt = conn.prepareStatement(sql);
//                pstmt.setString(1, elem);
//                pstmt.setInt(2, parentId);
//                pstmt.setString(3, index.getName());
//                ResultSet rs = pstmt.executeQuery();
//                if (rs.next()) {
//                    parentId = rs.getInt("id");
//                } else {
//                    return 0;
//                }
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return parentId;
    }

    private FileInfo getById(int id) {
        String sql = "SELECT * FROM filesystem WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                String path = getPathById(rs.getInt("id"));

                FileAttributes fa = new FileAttributes();
                fa.setSize(rs.getLong("fsize"));
                fa.setType(rs.getString("ftype").equals("d") ? FileType.DIRECTORY : FileType.FILE);
                fa.setLastModified(LocalDateTime.ofEpochSecond(rs.getLong("flast_modified"), 0, ZoneOffset.UTC));

                FileInfo fi = new FileInfo(
                        rs.getInt("id"),
                        rs.getInt("parent_id"),
                        path,
                        rs.getString("storage"),
                        fa
                );

                return fi;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
