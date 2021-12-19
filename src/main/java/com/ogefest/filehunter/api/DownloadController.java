package com.ogefest.filehunter.api;

import com.ogefest.filehunter.*;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.search.SearchResult;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.FileObject;
import com.ogefest.unifiedcloudfilesystem.UnifiedCloudFileSystem;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Path("/download")
public class DownloadController {

    @Inject
    App app;

    @GET
    @Path("/file")
    public Response get(@QueryParam("path") String path, @QueryParam("index") String indexName ) {

        DirectoryIndexStorage indexStorage = new DirectoryIndexStorage(app.getConfiguration());
        DirectoryIndex index = indexStorage.getByName(indexName);

        EngineConfiguration ec = new EngineConfiguration(index.getConfiguration());


        UnifiedCloudFileSystem ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine(index.getName(), BackendEngineFactory.get(index.getType(), ec));


        FileObject obj = ucfs.getByPath(indexName, path);
        if (obj == null) {
            Response.ResponseBuilder response = Response.status(404, "Not found");
            response.entity("File not found");
            return response.build();
        }

        if (obj.getEngineItem().isDirectory()) {
            Response.ResponseBuilder response = Response.serverError();
            return response.build();
        }

        try {

            Response.ResponseBuilder response = Response.ok(ucfs.read(obj));
            if (MimeUtils.hasExtension(obj.getEngineItem().getExt())) {
                response.type(MimeUtils.guessMimeTypeFromExtension(obj.getEngineItem().getExt()));
            } else {
                response.header("Content-Disposition", "attachment;filename=" + obj.getEngineItem().getName());
                response.type(MediaType.APPLICATION_OCTET_STREAM);
            }

            return response.build();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Response.ResponseBuilder response = Response.serverError();
        return response.build();
    }

}
