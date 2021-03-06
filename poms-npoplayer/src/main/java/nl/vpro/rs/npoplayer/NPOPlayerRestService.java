/*
 * Copyright (C) 2018 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.rs.npoplayer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.npoplayer.PlayerRequest;
import nl.vpro.domain.npoplayer.PlayerResponse;

/**
 * @author r.jansen
 * @since 5.10
 */
@Path("/npoplayer")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface NPOPlayerRestService {

    @POST
    @Path("/request")
    PlayerResponse forMid(PlayerRequest request);
}
