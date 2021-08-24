package com.ogefest.filehunter;

import org.ini4j.Ini;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Configuration {
    private String filename;
    private Ini ini;

    public Configuration(String filename) throws IOException {
        this.filename = filename;

        ini = new Ini();
        ini.load(new FileReader(filename));

    }

    public String getValue(String key) {
        HashMap<String, String> data = new HashMap<>();
        data.put("storage.directory", "/tmp/filehunterstorage");

        return data.get(key);
    }

    public ArrayList<Directory> getDirectories() {

        ArrayList<Directory> result = new ArrayList<>();

        for (String sectionName : ini.keySet()) {

            Directory d = new Directory();
            d.setName(sectionName);

            Map<String, String> map = ini.get(sectionName);

            ArrayList<String> pathList = new ArrayList<>();
            for (String elem : Arrays.asList(map.get("path").split(","))) {
                pathList.add(elem);
            }
            d.setPath(pathList);

            result.add(d);
        }

        return result;
    }

}
