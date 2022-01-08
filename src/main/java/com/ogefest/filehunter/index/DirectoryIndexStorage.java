package com.ogefest.filehunter.index;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ogefest.filehunter.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectoryIndexStorage {

    private ArrayList<DirectoryIndex> directories = new ArrayList<>();
    private Configuration conf;
    private String directorySessionFile = "index-configuration.json";

    public DirectoryIndexStorage(Configuration conf) {
        this.conf = conf;

        loadSession();
    }

    public ArrayList<DirectoryIndex> getDirectories() {
        return directories;
    }

    public DirectoryIndex getByName(String name) {
        for (DirectoryIndex dir : directories) {
            if (dir.getName().equals(name)) {
                return dir;
            }
        }
        return null;
    }

    public void removeByName(String name) {
        DirectoryIndex d = getByName(name);
        directories.remove(d);

        saveSession();
    }

    public void setDirectory(DirectoryIndex dir) {
        int index = -1;

        for (DirectoryIndex d : directories) {
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

        try {
            File f = new File(getSessionDbPath());
            if (!f.exists()) {
                return;
            }
            Path configurationPath = Path.of(getSessionDbPath());
            String jsonString = Files.readString(configurationPath, StandardCharsets.UTF_8);

            List<DirectoryIndex> tmp = Arrays.asList(new GsonBuilder().create().fromJson(jsonString, DirectoryIndex[].class));
            directories.addAll(tmp);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private synchronized void saveSession() {

        FileOutputStream fout = null;
        try {

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(directories);

            fout = new FileOutputStream(getSessionDbPath());
            fout.write(json.getBytes(StandardCharsets.UTF_8));
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
