package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.DirectoryIndex;
import com.ogefest.filehunter.DirectoryIndexStorage;
import com.ogefest.filehunter.task.ReindexStructure;
import com.ogefest.filehunter.task.RemoveIndex;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Path("/index")
public class IndexController {

    @Inject
    App app;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DirectoryIndex> hello() {
        DirectoryIndexStorage storage = new DirectoryIndexStorage(app.getConfiguration());
        return storage.getDirectories();
    }

    @POST
    @Path("/reindex/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public void reindexByName(@PathParam("name") String name) {

        DirectoryIndexStorage storage = new DirectoryIndexStorage(app.getConfiguration());
        ArrayList<DirectoryIndex> dirs = storage.getDirectories();

        DirectoryIndex d = storage.getByName(name);
        app.addTask(new ReindexStructure(d));
    }

    @POST
    @Path("/set")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DirectoryIndex> create(DirectoryIndex newDirectoryIndex) {

        if (newDirectoryIndex.getName().equals("")) {
            throw new BadRequestException("Name parameter is required");
        }
        if (newDirectoryIndex.getConfiguration().length() == 0) {
            throw new BadRequestException("Configuration parameter is required ");
        }
        if (newDirectoryIndex.getType().length() == 0) {
            throw new BadRequestException("Type parameter is required ");
        }

        DirectoryIndexStorage storage = new DirectoryIndexStorage(app.getConfiguration());
        storage.setDirectory(newDirectoryIndex);

        return storage.getDirectories();
    }

    @GET
    @Path("/get/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public DirectoryIndex getByName(@PathParam("name") String name) {
        DirectoryIndexStorage storage = new DirectoryIndexStorage(app.getConfiguration());
        DirectoryIndex result = storage.getByName(name);
        if (result == null) {
            throw new NotFoundException();
        }

        return result;
    }

    @DELETE
    @Path("/remove/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DirectoryIndex> remove(@PathParam("name") String name) {
        DirectoryIndexStorage storage = new DirectoryIndexStorage(app.getConfiguration());

        DirectoryIndex d = storage.getByName(name);
        if (d == null) {
            throw new NotFoundException();
        }

        storage.removeByName(name);
        app.addTask(new RemoveIndex(name));

        return storage.getDirectories();
    }

}