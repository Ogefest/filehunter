package com.ogefest.filehunter;

import com.ogefest.filehunter.index.*;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.filehunter.task.ReindexFullText;
import com.ogefest.filehunter.task.ReindexStructure;
import com.ogefest.filehunter.task.Task;
import com.ogefest.filehunter.task.Worker;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.FileObject;
import com.ogefest.unifiedcloudfilesystem.ResourceAccessException;
import com.ogefest.unifiedcloudfilesystem.UnifiedCloudFileSystem;
import io.quarkus.scheduler.Scheduled;

import javax.inject.Singleton;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

//@ApplicationScoped
@Singleton
public class App {

    private ArrayList<Task> tasks = new ArrayList<>();
    private Configuration conf;
    private Worker taskWorker;
    private FileSystemDatabase dbStorage;
    private Status indexStatus;

    public App() {
        conf = new Configuration();
        taskWorker = new Worker(conf);
        indexStatus = new Status();
    }

    public Configuration getConfiguration() {
        return conf;
    }

    public void addTask(Task t) {
        tasks.add(t);
    }


    @Scheduled(every = "60s")
    protected void checkIfIndexIsAvailable() {
        DirectoryIndexStorage storage = new DirectoryIndexStorage(conf);
        for (DirectoryIndex d : storage.getDirectories()) {
            EngineConfiguration ec = new EngineConfiguration(d.getConfiguration());

            UnifiedCloudFileSystem ucfs = new UnifiedCloudFileSystem();
            ucfs.registerEngine(d.getName(), BackendEngineFactory.get(d.getType(), ec));
            FileObject rootPath = ucfs.getByPath(d.getName(), "/");

            try {
                ArrayList<FileObject> itemList = ucfs.list(rootPath);
                if (itemList.size() > 0) {
                    indexStatus.set(d.getName(), StatusType.ONLINE);
                } else {
                    indexStatus.set(d.getName(), StatusType.OFFLINE);
                }

            } catch (IOException e) {
                indexStatus.set(d.getName(), StatusType.ERROR);
                e.printStackTrace();
            } catch (ResourceAccessException e) {
                indexStatus.set(d.getName(), StatusType.ERROR);
                e.printStackTrace();
            }
            try {
                ucfs.unregisterEngine(d.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Scheduled(every = "10s")
    protected void checkRecurringIndexing() {

        Thread.currentThread().setName("Add tasks");

        if (taskWorker.isBusy()) {
            return;
        }

        DirectoryIndexStorage storage = new DirectoryIndexStorage(conf);
        for (DirectoryIndex d : storage.getDirectories()) {
            if (d.getReindexType() != ReindexType.RECURRING) {
                return;
            }
            if (indexStatus.get(d.getName()) != StatusType.ONLINE) {
                return;
            }

            if (d.getLastStructureIndexed().plusSeconds(d.getIntervalUpdateStructure()).isBefore(LocalDateTime.now())) {
                addTask(new ReindexStructure(d));
                addTask(new ReindexFullText());

                d.setLastStructureIndexed(LocalDateTime.now());
                storage.setDirectory(d);
            }
        }
    }

    @Scheduled(every = "3s")
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

    public Status getIndexStatus() {
        return indexStatus;
    }

}
