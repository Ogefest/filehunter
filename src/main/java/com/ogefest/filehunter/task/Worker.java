package com.ogefest.filehunter.task;

import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.storage.Factory;
import com.ogefest.filehunter.storage.FileSystemDatabase;
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
        if (thread != null && thread.isAlive()) {
            return;
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {


                for (Task t : todo) {
                    currentTask = t;

                    LOG.info("Task " + t.getClass().getName() + " started");

//                    IndexRead indexRead = new IndexRead(conf);
//                    IndexWrite indexWrite = new IndexWrite(conf);
                    FileSystemDatabase db = Factory.get(conf);

                    currentTask.setConfiguration(conf);
//                    currentTask.setIndexes(indexWrite, indexRead);
                    currentTask.setDatabase(db);

                    currentTask.run();

//                    indexRead.closeIndex();
//                    indexWrite.closeIndex();
                    db.closeConnection();

                    LOG.info("Task " + t.getClass().getName() + " finished");
                    currentTask = null;

                }

//                isBusy = false;

            }
        });
//        isBusy = true;

        thread.setName("Task queue worker");
        thread.start();
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public boolean isBusy() {
        if (thread == null) {
            return false;
        }
        if (thread.isAlive()) {
            return true;
        }

        return false;
    }

}
