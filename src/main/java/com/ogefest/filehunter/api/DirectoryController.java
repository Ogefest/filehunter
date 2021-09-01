package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.Directory;
import com.ogefest.filehunter.DirectoryStorage;
import com.ogefest.filehunter.task.IndexStructure;

import javax.ws.rs.*;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Path("/directory")
public class DirectoryController {

    @Inject
    App app;

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Directory> hello() {
        DirectoryStorage storage = new DirectoryStorage(app.getConfiguration());
        return storage.getDirectories();
    }

    @POST
    @Path("/reindex")
    @Produces(MediaType.APPLICATION_JSON)
    public void reindex() {

        DirectoryStorage storage = new DirectoryStorage(app.getConfiguration());
        ArrayList<Directory> dirs = storage.getDirectories();

        for (Directory d : dirs) {
            app.addTask(new IndexStructure(d, app.getIndexForWrite(), app.getIndexForRead()));
        }
    }

    @POST
    @Path("/set")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Directory> create(Directory newDirectory) {

        if (newDirectory.getName().equals("")) {
            throw new BadRequestException("Name parameter is required");
        }
        if (newDirectory.getPath().size() == 0) {
            throw new BadRequestException("Path parameter is required ");
        }
        for (String p : newDirectory.getPath()) {
            File f = new File(p);
            if (!f.exists()) {
                throw new BadRequestException("Path " + p + " not exists");
            }
        }


        DirectoryStorage storage = new DirectoryStorage(app.getConfiguration());
        storage.setDirectory(newDirectory);

        return storage.getDirectories();
    }

    @GET
    @Path("/get/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Directory getByName(@PathParam("name") String name) {
        DirectoryStorage storage = new DirectoryStorage(app.getConfiguration());
        Directory result = storage.getByName(name);
        if (result == null) {
            throw new NotFoundException();
        }

        return result;
    }

    @DELETE
    @Path("/remove/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Directory> remove(String name) {
        DirectoryStorage storage = new DirectoryStorage(app.getConfiguration());

        Directory d = storage.getByName(name);
        if (d == null) {
            throw new NotFoundException();
        }

        storage.removeByName(name);

        return storage.getDirectories();
    }

}