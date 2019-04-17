package nl.vpro.api.rs.subtitles;

import java.io.InputStream;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;

import nl.vpro.domain.subtitles.Cue;
import nl.vpro.domain.subtitles.WEBVTTandSRT;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Provider
@Consumes(Constants.VTT)
public class VTTReader extends AbstractIteratorReader {

    public VTTReader() {
        super(Constants.VTT_TYPE);
    }

    @Override
    protected Iterator<Cue> read(InputStream entityStream) {
        return WEBVTTandSRT.parseWEBVTT(null, entityStream).getCues().iterator();
    }
}
