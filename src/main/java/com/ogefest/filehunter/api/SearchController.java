package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.IndexRead;
import com.ogefest.filehunter.SearchResult;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Path("/search")
public class SearchController {

    @Inject
    App app;

    @GET
    public ArrayList<SearchResult> search(@QueryParam("q") String query, @Context UriInfo uinfo) {

        MultivaluedMap<String, String> abc = uinfo.getQueryParameters();
        HashMap<String, String> filters = new HashMap<>();
        String kkey = "";
        for (String k : abc.keySet()) {
            if (k.indexOf("filter[") == 0) {
                kkey = k.substring(7, k.length()-1);
                filters.put(kkey, abc.getFirst(k));
            }
        }

        IndexRead ir = new IndexRead(app.getConfiguration());
        ArrayList<SearchResult> result = ir.query(query, filters);

        return result;
    }
}
