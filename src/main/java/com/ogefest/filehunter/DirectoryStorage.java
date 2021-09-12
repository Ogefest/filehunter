package com.ogefest.filehunter;

import java.io.*;
import java.util.ArrayList;

public class DirectoryStorage {

    private ArrayList<Directory> directories = new ArrayList<>();
    private Configuration conf;
    private String directorySessionFile = "session.db";

    public DirectoryStorage(Configuration conf) {
        this.conf = conf;

        loadSession();
    }

    public ArrayList<Directory> getDirectories() {
        return directories;
    }

    public Directory getByName(String name) {
        for (Directory dir : directories) {
            if (dir.getName().equals(name)) {
                return dir;
            }
        }
        return null;
    }

    public void removeByName(String name) {
        Directory d = getByName(name);
        directories.remove(d);

        saveSession();
    }

    public void setDirectory(Directory dir) {
        int index = -1;
        for (Directory d : directories) {
            if (d.getName().equals(dir.getName())) {
                index = directories.indexOf(d);
                break;
            }
        }

        if (index == -1) {
            directories.add(dir);
        } else {
            directories.set(index, dir);
        }

        saveSession();
    }

    private String getSessionDbPath() {
        String path = conf.getValue("storage.directory") + File.separator + directorySessionFile;

        File f = new File(path);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }

        return path;
    }

    private void loadSession() {

        FileInputStream streamIn = null;
        ObjectInputStream objectinputstream = null;
        try {
            streamIn = new FileInputStream(getSessionDbPath());
            objectinputstream = new ObjectInputStream(streamIn);
            directories = (ArrayList<Directory>) objectinputstream.readObject();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void saveSession() {

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(getSessionDbPath());
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(directories);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
