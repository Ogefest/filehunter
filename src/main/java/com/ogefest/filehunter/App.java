package com.ogefest.filehunter;

import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.task.IndexMetadata;
import com.ogefest.filehunter.task.IndexStructure;
import com.ogefest.filehunter.task.Task;
import com.ogefest.filehunter.task.Worker;
import io.quarkus.scheduler.Scheduled;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.ArrayList;

//@ApplicationScoped
@Singleton
public class App {

    private ArrayList<Task> tasks = new ArrayList<>();
    private Configuration conf;
    private Worker taskWorker;

    public App() {
        conf = new Configuration();
        taskWorker = new Worker(conf);
    }

    public Configuration getConfiguration() {
        return conf;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

    @Scheduled(every="10s")
    protected void checkRecurringIndexing() {

        Thread.currentThread().setName("Add tasks");

        if (taskWorker.isBusy()) {
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
    public void tasks() {

        Thread.currentThread().setName("Check queue");

        if (tasks.size() == 0) {
            return;
        }

        ArrayList<Task> todo = new ArrayList<>();
        todo.addAll(tasks);
        tasks.clear();

        if (!taskWorker.isBusy()) {
            taskWorker.run(todo);
        }

    }

    public IndexRead getIndexForRead() {
        return new IndexRead(conf);
    }

    public Task getCurrentTaskProcessing() {
        return taskWorker.getCurrentTask();
    }

}
