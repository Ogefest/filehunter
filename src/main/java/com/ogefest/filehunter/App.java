package com.ogefest.filehunter;

import com.ogefest.filehunter.task.IndexStructure;
import com.ogefest.filehunter.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class App {

    private ArrayList<Task> tasks = new ArrayList<Task>();
    private Configuration conf;
    private Logger logger = LoggerFactory.getLogger(App.class);

    public App() {

        try {
            conf = new Configuration("configuration.file");
//            indexStorage = new IndexWrite(conf);
//            indexRead = new IndexRead(conf.getValue("storage.directory"));
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
            addTask(new IndexStructure(d, getIndexForWrite(), getIndexForRead()));
        }

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

    public IndexWrite getIndexForWrite() {
        return new IndexWrite(conf);
    }

    public IndexRead getIndexForRead() {
        return new IndexRead(conf);
    }

}
