package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.domain.gtaa.*;
import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.*;
import nl.vpro.w3.rdf.Description;

import static nl.vpro.beeldengeluid.gtaa.OpenskosRepository.CONFIG_FILE;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class OpenskosTests {
    @Ignore
    @Test
    public void testPost1() {
        GTAARepository impl = getRealInstance();

        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk"  + System.currentTimeMillis())
            .build();
        impl.submit(pietjePuk, "POMS2");
    }

    @Test
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution() {
        GTAARepository impl = getRealInstance();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
                .givenName("Pietje")
                .familyName("Puk"  + System.currentTimeMillis())
                .build();

        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test(expected = GTAAConflict.class)
    @Ignore("Vervuilt GTAA")
    public void test409ConflictResolution3ShouldThrowException() {
        GTAARepository impl = getRealInstance();
        GTAANewPerson pietjePuk = GTAANewPerson.builder()
            .givenName("Pietje")
            .familyName("Puk"  + System.currentTimeMillis())
            .build();

        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
        impl.submit(pietjePuk, "POMS");
    }

    @Test
    @Ignore
    public void testFindPerson() {
        GTAARepository impl = getRealInstance();
        List<Description> persons = impl.findPersons("pietje", 100);
        assertThat(persons).isNotEmpty();
        System.out.println(persons);
        assertThat(persons.get(0).getStatus()).isNotNull();
    }

    @Test
    @Ignore
    public void testFindAnything() {
        GTAARepository impl = getRealInstance();
        List<Description> items = impl.findAnything("hilversum", 100);
        assertThat(items).isNotEmpty();
        System.out.println(items);
        assertThat(items.get(0).getStatus()).isNotNull();
    }

    @Test
    @Ignore
    public void testChanges() {
        GTAARepository impl = getRealInstance();
        Instant start = LocalDate.of(2017, 1, 1).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();
        Instant stop = LocalDate.now().atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            if (!record.isDeleted())
                assertThat(record.getMetaData().getFirstDescription().isPerson()).isTrue();
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
        assertThat(count).isGreaterThan(0);
    }

    @Test
    @Ignore
    public void testAllChanges() {
        GTAARepository impl = getRealInstance();
        Instant start = LocalDate.of(2017, 1, 1).atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();
        Instant stop = LocalDate.now().atStartOfDay().atZone(OpenskosRepository.ZONE_ID).toInstant();

        CountedIterator<Record> updates = impl.getAllUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            if (record.getMetaData() == null) {
                assertThat(record.getHeader().getStatus()).isEqualTo("deleted");
            }
            count++;
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        // TODO check out why count doesn't match
        // assertThat(count).isEqualTo(updates.getSize().get());
    }

    @Test
    @Ignore
    public void addPerson() {
        GTAARepository impl = getRealInstance();
        GTAANewPerson p = new GTAANewPerson();
        p.setFamilyName("asdasd");
        p.setGivenName("asdasd");
        //p.setListIndex(0);
        impl.submit(p, "demo-cms:gtaa-user");
    }

    @Test
    @Ignore
    public void testChangesRecent() {
        GTAARepository impl = getRealInstance();
        Instant start = Instant.now().minusSeconds(3600000);
        Instant stop = Instant.now();

        CountedIterator<Record> updates = impl.getPersonUpdates(start, stop);
        long count = 0;
        while (updates.hasNext()) {
            Record record = updates.next();
            count++;
            System.out.println(record);
            log.info("{}/{}: {}", updates.getCount(), updates.getSize().get(), record);

        }
        assertThat(count).isEqualTo(updates.getSize().get());
    }

    @Test
    public void testStatus() {
        GTAARepository impl = getRealInstance();
        impl.retrieveConceptStatus("bla");
    }

    OpenskosRepository getRealInstance() {
        return getRealInstance(Env.DEV);
    }

    static OpenskosRepository getRealInstance(final Env env) {
        Map<String, String> gtaa = ConfigUtils.filtered(env, "gtaa", ConfigUtils.getPropertiesInHome(CONFIG_FILE));
        final OpenskosRepository impl = ConfigUtils.configured(env, OpenskosRepository.class, gtaa);
        impl.init();
        return impl;
    }


}
