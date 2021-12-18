package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.SystemStatus;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.task.Task;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/system")
public class SystemController {

    @Inject
    App app;

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, String> ping() {
        HashMap<String, String> result = new HashMap<>();
        result.put("status", "ok");
        result.put("ts", Long.toString(System.currentTimeMillis()));

        return result;
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public SystemStatus status() {

        SystemStatus status = new SystemStatus();
        IndexRead reader = app.getIndexForRead();

        status.setDocsNumber(reader.getNumDocs());

        Task currentTask = app.getCurrentTaskProcessing();

        status.setCurrentTask("");
        if (currentTask != null) {
            status.setCurrentTask(currentTask.getTaskName());
        }


        return status;
    }
}
