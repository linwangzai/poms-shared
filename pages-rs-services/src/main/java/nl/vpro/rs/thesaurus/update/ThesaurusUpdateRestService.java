/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.rs.thesaurus.update;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.media.gtaa.GTAANewPerson;
import nl.vpro.domain.media.gtaa.GTAANewThesaurusObject;
import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.domain.media.gtaa.ThesaurusObject;

/**
 * @author Machiel Groeneveld
 * @since 5.5
 */

@Path(ThesaurusUpdateRestService.PATH)
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public interface ThesaurusUpdateRestService {

    String AUTHENTICATION_SCHEME = "Bearer";



    String PATH = "/thesaurus/";
    String TAG = "thesaurus";

    @POST
    @Path("/person")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    GTAAPerson submit(@HeaderParam(HttpHeaders.AUTHORIZATION) String jws, @NotNull GTAANewPerson person);

    @POST
    @Path("/item")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    ThesaurusObject submit(@HeaderParam(HttpHeaders.AUTHORIZATION) String jws, @NotNull GTAANewThesaurusObject thesaurusObject);

}
