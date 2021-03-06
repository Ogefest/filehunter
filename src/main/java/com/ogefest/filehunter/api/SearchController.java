package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.FileItem;
import com.ogefest.filehunter.storage.LuceneSearch;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;

@Path("/search")
public class SearchController {

    @Inject
    App app;

    @GET
    public ArrayList<FileItem> search(@QueryParam("q") String query, @Context UriInfo uinfo) {

        MultivaluedMap<String, String> abc = uinfo.getQueryParameters();
        HashMap<String, String> filters = new HashMap<>();
        String kkey = "";
        for (String k : abc.keySet()) {
            if (k.indexOf("filter[") == 0) {
                kkey = k.substring(7, k.length() - 1);
                filters.put(kkey, abc.getFirst(k));
            }
        }

        LuceneSearch ir = new LuceneSearch(app.getConfiguration());
        ArrayList<FileInfo> fileList = ir.query(query, filters);

        ArrayList<FileItem> result = new ArrayList<>();
        for (FileInfo fi : fileList) {
            result.add(new FileItem(fi));
        }

        return result;
    }
}
