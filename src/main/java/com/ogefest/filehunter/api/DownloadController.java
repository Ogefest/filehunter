package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.IndexRead;
import com.ogefest.filehunter.MimeUtils;
import com.ogefest.filehunter.SearchResult;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

@Path("/download")
public class DownloadController {

    @Inject
    App app;

    @GET
    @Path("/{uuid}")
    public Response file(@PathParam("uuid") String uuid) {

        IndexRead ir = app.getIndexForRead();
        SearchResult res = ir.getByUuid(uuid);
        if (res == null) {
            throw new NotFoundException();
        }
        ir.closeIndex();

        File file = new File(res.getPath());
        if (!file.isFile()) {
            throw new BadRequestException("Only file available for download");
        }
        if (!file.exists()) {
            throw new NotFoundException();
        }

        Response.ResponseBuilder response = Response.ok(file);
        if (MimeUtils.hasExtension(res.getExt())) {
            response.type(MimeUtils.guessMimeTypeFromExtension(res.getExt()));
        } else {
            response.header("Content-Disposition", "attachment;filename=" + file.getName());
            response.type(MediaType.APPLICATION_OCTET_STREAM);
        }

        return response.build();
    }
}
