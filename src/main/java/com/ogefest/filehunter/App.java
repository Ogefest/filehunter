package com.ogefest.filehunter;

import com.ogefest.filehunter.task.IndexStructure;
import com.ogefest.filehunter.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class App {

    private ArrayList<Task> tasks = new ArrayList<Task>();
    private IndexQuery indexQuery;
    private IndexStorage indexStorage;
    private Configuration conf;
    private Logger logger = LoggerFactory.getLogger(App.class);

    public App() {

        try {
            conf = new Configuration("configuration.file");
            indexStorage = new IndexStorage(conf.getValue("storage.directory"));
            indexQuery = new IndexQuery(conf.getValue("storage.directory"));
        } catch (IOException e) {
            logger.error("Unable to read file " + e.getMessage());
            e.printStackTrace();
            return;
        }


    }

    public Configuration getConfiguration() {
        return conf;
    }

    public void reindex() {
        ArrayList<Directory> dirs = conf.getDirectories();
        for (Directory d : dirs) {
            addTask(new IndexStructure(d, indexStorage, indexQuery));
        }

//        indexStorage.finish();
    }

    public void addTask(Task t) {
        tasks.add(t);
        tasks();
    }

    private void tasks() {
        for(Task t : tasks) {
            t.run();
        }
        tasks.clear();
    }

    public ArrayList<SearchResult> search(String query) {
        return indexQuery.query(query);
    }

    public SearchResult getByUuid(String uuid) {
        return indexQuery.getByUuid(uuid);
    }

}
