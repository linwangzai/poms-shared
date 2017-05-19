package nl.vpro.api.rs.subtitles;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesFormat;
import nl.vpro.domain.subtitles.SubtitlesUtil;

import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Provider
@Produces(VTT)
public class VTTSubtitlesWriter extends AbstractSubtitlesWriter {


    public VTTSubtitlesWriter() {
        super(SubtitlesFormat.WEBVTT);
    }

    @Override
    protected void stream(Subtitles subtitles, OutputStream entityStream) throws IOException {
        SubtitlesUtil.toVTT(iterate(subtitles), entityStream);
    }
}
