package com.ogefest.filehunter.task;

import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.search.IndexWrite;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.filehunter.storage.SqliteFSD;
import org.jboss.logging.Logger;

import java.util.ArrayList;

public class Worker {

    private static final Logger LOG = Logger.getLogger(Worker.class);
    private Thread thread;
    private boolean isBusy = false;
    private Configuration conf;
    private Task currentTask;

    public Worker(Configuration conf) {
        this.conf = conf;
    }

    public void run(ArrayList<Task> todo) {
        if (isBusy) {
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                IndexRead indexRead = new IndexRead(conf);
                IndexWrite indexWrite = new IndexWrite(conf);
                FileSystemDatabase db = new SqliteFSD(conf);

                for (Task t : todo) {
                    currentTask = t;
                    LOG.info("Task " + t.getClass().getName() + " started");

                    currentTask.setConfiguration(conf);
                    currentTask.setIndexes(indexWrite, indexRead);
                    currentTask.setDatabase(db);

                    currentTask.run();

                    LOG.info("Task " + t.getClass().getName() + " finished");
                    currentTask = null;
                }

                indexRead.closeIndex();
                indexWrite.closeIndex();
                db.closeConnection();

                isBusy = false;

            }
        });
        isBusy = true;

        t.setName("Task queue worker");
        t.start();
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public boolean isBusy() {
        return isBusy;
    }

}
