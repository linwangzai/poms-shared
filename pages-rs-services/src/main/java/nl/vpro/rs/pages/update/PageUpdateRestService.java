/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.rs.pages.update;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.vpro.domain.page.update.PageUpdate;

/**
 * The Rest Interface is implemented by PageUpdateRestServiceImpl, but can also be used to generate clients.
 * @author Roelof Jan Koekoek
 * @since 3.0
 */

@Path(PageUpdateRestService.PATH)
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
public interface PageUpdateRestService {

    String PATH = "/pages/updates";

    @POST
    @Path("")
    Response save(
        @NotNull @Valid PageUpdate update
    );

    @DELETE
    @Path("")
    @Produces({"application/json", "application/xml"})
    Response delete(
        @QueryParam("url") @NotNull String url,
        @QueryParam("batch")  Boolean batch,
        @QueryParam("max") Integer max
        );

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("")
    PageUpdate load(@QueryParam("url") @NotNull String url);


}
