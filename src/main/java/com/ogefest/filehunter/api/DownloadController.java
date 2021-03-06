package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.BackendEngineFactory;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.MimeUtils;
import com.ogefest.filehunter.index.DirectoryIndex;
import com.ogefest.filehunter.index.DirectoryIndexStorage;
import com.ogefest.filehunter.storage.Factory;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.FileObject;
import com.ogefest.unifiedcloudfilesystem.UnifiedCloudFileSystem;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Path("/download")
public class DownloadController {

    @Inject
    App app;

    @GET
    @Path("/uid")
    public Response getByUid(@QueryParam("uid") String uid) {

        FileSystemDatabase db = Factory.get(app.getConfiguration());
        FileInfo fi = db.get(uid);
        if (fi == null) {
            Response.ResponseBuilder response = Response.status(404, "Not found");
            response.entity("File not found");
            return response.build();
        }

        DirectoryIndexStorage indexStorage = new DirectoryIndexStorage(app.getConfiguration());
        DirectoryIndex index = indexStorage.getByName(fi.getIndexName());

        EngineConfiguration ec = new EngineConfiguration(index.getConfiguration());


        UnifiedCloudFileSystem ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine(index.getName(), BackendEngineFactory.get(index.getType(), ec));

        FileObject obj = ucfs.getByPath(fi.getIndexName(), fi.getPath());
        if (obj == null) {
            Response.ResponseBuilder response = Response.status(404, "Not found");
            response.entity("File not found");
            return response.build();
        }

        if (obj.getEngineItem().isDirectory()) {
            Response.ResponseBuilder response = Response.serverError();
            response.entity("Internal error");
            return response.build();
        }

        try {

            InputStream in = ucfs.read(obj);
            Response.ResponseBuilder response = Response.ok(in);
            response.header("Content-length", in.available());
            response.header("Content-Disposition", "attachment;filename=" + obj.getEngineItem().getName());

            if (MimeUtils.hasExtension(obj.getEngineItem().getExt())) {
                response.type(MimeUtils.guessMimeTypeFromExtension(obj.getEngineItem().getExt()));
            } else {
                response.type(MediaType.APPLICATION_OCTET_STREAM);
            }

            return response.build();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Response.ResponseBuilder response = Response.serverError();
        return response.build();
    }

    @GET
    @Path("/file")
    public Response get(@QueryParam("path") String path, @QueryParam("index") String indexName) {

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
            response.entity("Internal error");
            return response.build();
        }

        try {

            InputStream in = ucfs.read(obj);
            Response.ResponseBuilder response = Response.ok(in);
            response.header("Content-length", in.available());
            response.header("Content-Disposition", "inline;filename=" + obj.getEngineItem().getName());

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
