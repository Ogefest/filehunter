package com.ogefest.filehunter.api;

import io.vertx.ext.web.Router;

import javax.enterprise.event.Observes;
import javax.ws.rs.Path;

@Path("/gui")
public class GuiController {

    public void init(@Observes Router router) {
        router.route("/gui/*").handler(routingContext -> routingContext.reroute("/"));
    }


}
