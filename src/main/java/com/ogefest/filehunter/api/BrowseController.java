package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.index.DirectoryIndexStorage;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.FileItem;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.filehunter.storage.H2FSDReadOnly;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;

@Path("/browse")
public class BrowseController {

    @Inject
    App app;

    @GET
    public ArrayList<FileItem> search(@QueryParam("path") String query, @QueryParam("index") String indexname) {

        FileSystemDatabase db = new H2FSDReadOnly(app.getConfiguration());
        DirectoryIndexStorage storage = new DirectoryIndexStorage(app.getConfiguration());

        ArrayList<FileItem> result = new ArrayList<>();

        if (storage.getByName(indexname) == null) {
            return result;
        }

        ArrayList<FileInfo> fileList = db.list(query, storage.getByName(indexname));

        for (FileInfo fi : fileList) {
            result.add(new FileItem(fi));
        }

        return result;

    }
}
