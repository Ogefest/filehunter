package com.ogefest.filehunter;

//import org.ini4j.Ini;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Configuration {
    private String filename;
    private HashMap<String, String> configuration = new HashMap<>();

    public Configuration() {

        /*
        DEFAULT VALUES
         */
        configuration.put("storage.directory", new File("filehunterstorage").getAbsolutePath());

        Map<String, String> envs = System.getenv();
        for (String key : envs.keySet()) {
            if (key.indexOf("filehunter") == 0) {
                configuration.put(key.substring(11), envs.get(key));
            }
         }

//        configuration.put("storage.directory", System.getenv("filehunter.storage.directory"));
//        if (configuration.get("storage.directory") == null) {
//            configuration.put("storage.directory", new File("filehunterstorage").getAbsolutePath());
//        }

    }

    public String getValue(String key) {
        return configuration.get(key);
//        HashMap<String, String> data = new HashMap<>();
////        data.put("storage.directory", "./filehunterstorage");
//        data.put("storage.directory", new File("filehunterstorage").getAbsolutePath());
//
//        return data.get(key);
    }

//    public ArrayList<Directory> getDirectoriesFromConfig() {
//
//        ArrayList<Directory> result = new ArrayList<>();
//
//        for (String sectionName : ini.keySet()) {
//
//            Directory d = new Directory();
//            d.setName(sectionName);
//
//            Map<String, String> map = ini.get(sectionName);
//
//            ArrayList<String> pathList = new ArrayList<>();
//            for (String elem : Arrays.asList(map.get("path").split(","))) {
//                pathList.add(elem);
//            }
//            d.setPath(pathList);
//
//            result.add(d);
//        }
//
//        return result;
//    }
}
