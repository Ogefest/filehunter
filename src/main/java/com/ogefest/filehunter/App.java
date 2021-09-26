package com.ogefest.filehunter;

import com.ogefest.filehunter.task.IndexMetadata;
import com.ogefest.filehunter.task.IndexStructure;
import com.ogefest.filehunter.task.Task;
import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.ArrayList;

//@ApplicationScoped
@Singleton
public class App {

    private static final Logger LOG = Logger.getLogger(App.class);
    private ArrayList<Task> tasks = new ArrayList<>();
    private Task currentTask;
    private boolean taskQueueLocked = false;
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

    @Scheduled(every="10s")
    protected synchronized void checkRecurringIndexing() {

        if (taskQueueLocked) {
            return;
        }

        DirectoryIndexStorage storage = new DirectoryIndexStorage(conf);
        for (DirectoryIndex d : storage.getDirectories()) {
            if (d.getLastStructureIndexed().plusSeconds(d.getIntervalUpdateStructure()).isBefore(LocalDateTime.now())) {
                addTask(new IndexStructure(d));
            }
        }

        DirectoryIndexStorage storage2 = new DirectoryIndexStorage(conf);
        for (DirectoryIndex d : storage.getDirectories()) {
                addTask(new IndexMetadata(d));
        }

    }


    @Scheduled(every="3s")
    public synchronized void tasks() {

        if (tasks.size() == 0) {
            return;
        }
        if (taskQueueLocked) {
            return;
        }
        taskQueueLocked = true;

        ArrayList<Task> todo = new ArrayList<>();
        todo.addAll(tasks);
        tasks.clear();

        IndexRead indexRead = new IndexRead(conf);
        IndexWrite indexWrite = new IndexWrite(conf);

        for(Task t : todo) {
            currentTask = t;
            LOG.debug("Task " + t.getClass().getName() + " started");

            currentTask.setApp(this);
            currentTask.setIndexes(indexWrite, indexRead);
            currentTask.run();

            LOG.debug("Task " + t.getClass().getName() + " finished");
            currentTask = null;
        }

        indexRead.closeIndex();
        indexWrite.closeIndex();

        taskQueueLocked = false;
    }

//    private
//    public IndexWrite getIndexForWrite() {
//        return new IndexWrite(conf);
//    }
//
    public IndexRead getIndexForRead() {
        return new IndexRead(conf);
    }

    public Task getCurrentTaskProcessing() {
        return currentTask;
    }

}
