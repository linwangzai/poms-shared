package nl.vpro.domain.media.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nl.vpro.domain.media.TvaCountry;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public class TvaCountryAdapter extends XmlAdapter<String, TvaCountry> {
    @Override
    public TvaCountry unmarshal(String v) throws Exception {
        return TvaCountry.find(v);
    }

    @Override
    public String marshal(TvaCountry v) throws Exception {
        return v.name();
    }
}
