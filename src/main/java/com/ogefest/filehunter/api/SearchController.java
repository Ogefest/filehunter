package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.IndexRead;
import com.ogefest.filehunter.SearchResult;
//import org.jboss.resteasy.reactive.RestQuery;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Map;

@Path("/search")
public class SearchController {

    @Inject
    App app;

    @GET
    public ArrayList<SearchResult> search(@QueryParam("q") String query) {

        IndexRead ir = new IndexRead(app.getConfiguration());
        ArrayList<SearchResult> result = ir.query(query);

        return result;
    }
}
