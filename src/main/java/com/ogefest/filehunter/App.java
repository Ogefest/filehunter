package com.ogefest.filehunter;

import com.ogefest.filehunter.task.IndexStructure;
import com.ogefest.filehunter.task.Task;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;
import io.quarkus.scheduler.ScheduledExecution;

//@ApplicationScoped
@Singleton
public class App {

    private static final Logger LOG = Logger.getLogger(App.class);
    private ArrayList<Task> tasks = new ArrayList<>();
    private Configuration conf;

    public App() {
        conf = new Configuration();
    }

    public Configuration getConfiguration() {
        return conf;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    @Scheduled(every="100s")
    protected synchronized void checkRecurringIndexing() {
        DirectoryStorage storage = new DirectoryStorage(conf);
        for (Directory d : storage.getDirectories()) {
            if (d.getLastStructureIndexed().plusSeconds(d.getIntervalUpdateStructure()).isBefore(LocalDateTime.now())) {
                addTask(new IndexStructure(d));
            }
        }
    }

    @Scheduled(every="3s")
    public synchronized void tasks() {

        if (tasks.size() == 0) {
            return;
        }

        ArrayList<Task> todo = new ArrayList<>();
        todo.addAll(tasks);
        tasks.clear();

        for(Task t : todo) {
            LOG.info("Task " + t.getClass().getName() + " started");
            t.setApp(this);
            t.run();
            LOG.info("Task " + t.getClass().getName() + " finished");
        }
    }

    public IndexWrite getIndexForWrite() {
        return new IndexWrite(conf);
    }

    public IndexRead getIndexForRead() {
        return new IndexRead(conf);
    }

}
