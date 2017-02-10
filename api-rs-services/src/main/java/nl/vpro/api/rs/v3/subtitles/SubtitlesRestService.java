package nl.vpro.api.rs.v3.subtitles;

import java.util.Locale;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import nl.vpro.domain.api.subtitles.SubtitlesForm;
import nl.vpro.domain.api.subtitles.SubtitlesSearchResult;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.api.rs.subtitles.Constants.*;
import static nl.vpro.domain.api.Constants.*;


/**
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Path(SubtitlesRestService.PATH)
@Produces({VTT, EBU, SRT})// APPLICATION_JSON, APPLICATION_XML})
public interface SubtitlesRestService {

    String TAG = "subtitles";
    String PATH = "/" + TAG;

    @GET
    @Path("/{mid}/{language}/{type}")
    Subtitles get(
        @PathParam("mid") String mid,
        @PathParam("language") Locale locale,
        @PathParam("type") SubtitlesType type
    );

    @GET
    @Path("/{mid}/{language}")
    Subtitles get(
        @PathParam("mid") String mid,
        @PathParam("language") Locale locale);

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    SubtitlesSearchResult search(
        @Valid SubtitlesForm form,
        @QueryParam(OFFSET) @DefaultValue(ZERO) @Min(0) Long offset,
        @QueryParam(MAX) @DefaultValue(DEFAULT_MAX_RESULTS_STRING) Integer max);

}
