package nl.vpro.api.rs.v3.thesaurus;

import java.io.InputStream;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.Constants;
import nl.vpro.domain.api.thesaurus.ThesaurusObject;
import org.jboss.resteasy.annotations.cache.NoCache;

import nl.vpro.domain.api.thesaurus.ThesaurusResult;

import static nl.vpro.domain.api.Constants.*;

@SuppressWarnings("RestParamTypeInspection")
@Path(ThesaurusRestService.PATH)
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ThesaurusRestService {
    String TAG = "thesaurus";
    String PATH = "/" + TAG;
    String ID = "mid";
    String SORT = "sort";
    String SINCE = "since";
    String PUBLISHEDSINCE = "publishedSince";
    String CHECK_PROFILE = "checkProfile";
    String DELETES = "deletes";



    @GET
    @Path("/people")
    ThesaurusResult.PersonList findPersons(
            @QueryParam("text") @DefaultValue("") String text,
            @QueryParam(MAX) @DefaultValue(Constants.DEFAULT_MAX_RESULTS_STRING) Integer max);




    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/people/updates")
    @NoCache
    InputStream listPersonUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
        ) throws Exception;


    @GET
    @Path("/items")
    ThesaurusResult<ThesaurusObject> listItems(@QueryParam("text") @DefaultValue("") String text,
                                               @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);


    @GET
    @Produces({ MediaType.APPLICATION_JSON})
    @Path("/items/updates")
    @NoCache
    InputStream listItemUpdates(
        @QueryParam("from") Instant from,
        @QueryParam("to") Instant to,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    ) throws Exception;

    @GET
    @Path("/items/status")
    @NoCache
    ThesaurusObject itemStatus(
        @QueryParam("id") String id
    ) throws Exception;


}
