package com.ogefest.filehunter;

//import org.ini4j.Ini;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {
    private String filename;
    private HashMap<String, String> configuration = new HashMap<>();

    public Configuration() {

        /*
        DEFAULT VALUES
         */
        configuration.put("storage.directory", new File("filehunterstorage").getAbsolutePath());

        Properties props = System.getProperties();
        for (String key : props.stringPropertyNames()) {
            if (key.indexOf("filehunter") == 0) {
                configuration.put(key.substring(11), props.get(key).toString());
            }
        }

        Map<String, String> envs = System.getenv();
        for (String key : envs.keySet()) {
            if (key.indexOf("filehunter") == 0) {
                configuration.put(key.substring(11), envs.get(key));
            }
        }

    }

    public String getValue(String key) {
        return configuration.get(key);
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
