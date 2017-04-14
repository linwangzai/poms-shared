package nl.vpro.rs.media;

import java.io.InputStream;
import java.time.Duration;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static nl.vpro.rs.media.MediaBackendRestService.ERRORS;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Path("/frames")
public interface FrameCreatorRestService {

    @PUT
    @Path("/{mid}/{offset}")
    @Produces(MediaType.TEXT_PLAIN)
    Response createFrame(
        @PathParam("mid") String mid,
        @PathParam("offset") Duration duration,
        @QueryParam(ERRORS) String errors,
        InputStream stream
    );
}
