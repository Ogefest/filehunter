package com.ogefest.filehunter.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.FileAttributes;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.index.DirectoryIndex;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.serializer.SerializerInteger;
import org.mapdb.serializer.SerializerString;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.CRC32;

public class MapDbFSD implements FileSystemDatabase {

    private Configuration conf;
    private DB filesystem;
    private HTreeMap<String, String> files;
    private HTreeMap<String, String> finfo;
    //    private HTreeMap<String, String> fattr;
    private HTreeMap<String, Integer> ftsStatus;
    private String sessionId;

    public MapDbFSD(Configuration conf) {
        this.conf = conf;

        String dbFilePath = conf.getValue("storage.directory") + "/filesystem.db";

        filesystem = DBMaker.fileDB(dbFilePath).make();


        files = filesystem.hashMap("files")
                .keySerializer(new SerializerString())
                .valueSerializer(new SerializerString())
                .createOrOpen();

        finfo = filesystem.hashMap("finfo")
                .keySerializer(new SerializerString())
                .valueSerializer(new SerializerString())
                .createOrOpen();

//        fattr = filesystem.hashMap("fattr")
//                .keySerializer(new SerializerString())
//                .valueSerializer(new SerializerString())
//                .createOrOpen();

        ftsStatus = filesystem.hashMap("fts")
                .keySerializer(new SerializerString())
                .valueSerializer(new SerializerInteger())
                .createOrOpen();

    }

    private String getHash(String path, String index) {
        CRC32 fileCRC32 = new CRC32();
        fileCRC32.update(path.getBytes(StandardCharsets.UTF_8));
        return String.format(Locale.US, "%08X", fileCRC32.getValue()) + "-" + index;
    }

    @Override
    public void clear(FileInfo fi) {
        String k = getHash(fi.getPath(), fi.getIndexName());
        files.remove(k);
        finfo.remove(k);
        ftsStatus.remove(k);
    }

    @Override
    public FileInfo add(String path, FileAttributes attributes, DirectoryIndex index) {
        String k = getHash(path, index.getName());

        String[] elems = path.split("/");
        String parentPath = "";
        if (elems.length > 1) {
            for (int i = 0; i < elems.length - 1; i++) {
                if (elems[i].length() > 0) {
                    parentPath = parentPath + "/" + elems[i];
                }
            }
        }
        String parentHash = getHash(parentPath, index.getName());
        String parentElements = files.get(parentHash);
        if (parentElements == null || parentElements.length() == 0) {
            parentElements = k;
        } else {
            parentElements = parentElements + "," + k;
        }
        String[] arrayOfParentElements = parentElements.split(",");
        String[] uniqueParentElements = arrayOfParentElements;
        String newChildList = "";
        for (String ch : uniqueParentElements) {
            newChildList = newChildList + "," + ch;
        }
        newChildList = newChildList.substring(1);
        files.put(parentHash, newChildList);

//        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        FileInfo fi = new FileInfo(k, parentHash, path, index.getName(), attributes);

        Gson gson = new GsonBuilder().create();
        String fjson = gson.toJson(fi);
        finfo.put(k, fjson);

//        String attrJson = gson.toJson(attributes);
//        fattr.put(k, attrJson);


        return get(k);
    }

    @Override
    public FileInfo get(String path, DirectoryIndex index) {
        String k = getHash(path, index.getName());
        return get(k);
    }

    @Override
    public FileInfo get(String uid) {
        String jsonString = finfo.get(uid);
//        String jsonAttr = fattr.get(uid);

        FileInfo fi = new GsonBuilder().create().fromJson(jsonString, FileInfo.class);
//        FileAttributes fa = new GsonBuilder().create().fromJson(jsonAttr, FileAttributes.class);
//        fi.setFileAttributes(fa);

        return fi;
    }

    @Override
    public ArrayList<FileInfo> list(String path, DirectoryIndex index) {
        if (path.equals("/")) {
            path = "";
        }
        String k = getHash(path, index.getName());
        return list(k);
    }

    @Override
    public ArrayList<FileInfo> list(String uid) {
        ArrayList<FileInfo> result = new ArrayList<>();

        String keys = files.get(uid);
        if (keys == null || keys.length() == 0) {
            return result;
        }
        String[] elems = keys.split(",");


        for (String e : elems) {
            result.add(get(e));
        }
        return result;
    }

    @Override
    public boolean exists(FileInfo fi) {
        return finfo.containsKey(getHash(fi.getPath(), fi.getIndexName()));
    }

    @Override
    public boolean exists(String path, DirectoryIndex index) {
        return finfo.containsKey(getHash(path, index.getName()));
    }

    @Override
    public void setCurrentStatus(FileInfo fi, int counter) {
//        return null;
//        String k = getHash(fi.getPath(), fi.getIndexName());
//        ftsStatus.put(k, counter);
    }

    @Override
    public void setCurrentFTSStatus(FileInfo fi, int ftsStatus) {
        if (fi.getPath() == null || fi.getIndexName() == null) {
            return;
        }
        String k = getHash(fi.getPath(), fi.getIndexName());

        if (ftsStatus != FTSStatus.TO_ADD.getValue()) {
            this.ftsStatus.remove(k);
            return;
        }

        this.ftsStatus.put(k, ftsStatus);
    }

    @Override
    public void setCurrentAttributes(FileInfo fi, FileAttributes attributes) {
        String k = getHash(fi.getPath(), fi.getIndexName());
        FileInfo newFi = new FileInfo(fi.getId(), fi.getParentId(), fi.getPath(), fi.getIndexName(), attributes);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(newFi);

        finfo.put(k, json);
    }

    @Override
    public ArrayList<FileInfo> getItemsToClear() {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<FileInfo> getItemsToFullTextIndex() {

        ArrayList<FileInfo> result = new ArrayList<>();
        for (String k : ftsStatus.getKeys()) {
            result.add(get(k));
        }

        return result;
    }

    @Override
    public void openReindexingSession(int sessionId, DirectoryIndex index) {

    }

    @Override
    public void closeReindexingSession(int sessionId, DirectoryIndex index) {
        filesystem.commit();
    }

    @Override
    public void closeConnection() {
        filesystem.commit();
//        filesystem.close();
    }
}
