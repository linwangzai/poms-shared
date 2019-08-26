package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;

import nl.vpro.domain.media.GeoLocation;
import nl.vpro.domain.media.GeoLocations;
import nl.vpro.domain.media.GeoRoleType;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MediaObjectOwnableListsTest {

    /**
     * The values for the Owner with higher rank should be used for expansion
     */
    @Test
    public <OL extends AbstractMediaObjectOwnableList> void expandGeoLocation() {

        List<GeoLocation> geoLocation1 = Arrays.asList(
                GeoLocation.builder().name("Amsterdam").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        List<GeoLocation> geoLocation2 = Arrays.asList(
                GeoLocation.builder().name("Utrecht").scopeNote("City").gtaaUri("test/123").role(GeoRoleType.RECORDED_IN).build()
        );
        GeoLocations g1 = GeoLocations.builder().owner(OwnerType.MIS).values(geoLocation1).build();
        GeoLocations g2 = GeoLocations.builder().owner(OwnerType.WHATS_ON).values(geoLocation2).build();
        SortedSet set = new TreeSet();
        set.add(g2);
        set.add(g1);


        final SortedSet r = MediaObjectOwnableLists.expandOwnedList(set,
                (owner, values) -> GeoLocations.builder().values(values).owner(owner).build(),
                OwnerType.ENTRIES
        );

        SortedSet<OL> result = r;
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.stream().map(v -> v.getOwner() +":"+ ((GeoLocation)v.getValues().get(0)).getName()).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("BROADCASTER:Amsterdam","NPO:Amsterdam","MIS:Amsterdam", "WHATS_ON:Utrecht"));

        for(OL value : result){
            log.info(value.toString());
        }
    }
}