/*
 * Copyright (C) 2008 All rights reserved VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.validation.ConstraintViolation;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.media.MediaDomainTestHelper.validator;
import static nl.vpro.domain.media.support.OwnerType.CERES;
import static nl.vpro.domain.media.support.OwnerType.NEBO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class MediaObjectTest {

    @Test
    public void testIdFromUrn() {
        assertThat(Program.idFromUrn("urn:vpro:media:program:12463402")).isEqualTo(12463402L);
    }

    @Test
    public void testAddCrid() {
        MediaObject mediaObject = new Program();
        mediaObject.addCrid(null);
        assertThat(mediaObject.getCrids()).isEmpty();

        mediaObject.addCrid("Crid 1");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1");

        mediaObject.addCrid("Crid 2");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1", "Crid 2");

        mediaObject.addCrid("Crid 1");
        mediaObject.addCrid("Crid 2");
        assertThat(mediaObject.getCrids()).containsExactly("Crid 1", "Crid 2");
    }

    @Test
    public void testAddTitle() {
        MediaObject mediaObject = new Program();
        mediaObject.addTitle(null);
        assertThat(mediaObject.getTitles()).isEmpty();

        mediaObject.addTitle(new Title("Title 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getTitles().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getTitles()).hasSize(1);
        assertThat(mediaObject.getTitles().last().getTitle()).isEqualTo("Title 1");

        mediaObject.addTitle(new Title("Title 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().getTitle()).isEqualTo("Title 2");

        mediaObject.addTitle(new Title("Title 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getTitles()).hasSize(2);
        assertThat(mediaObject.getTitles().last().getTitle()).isEqualTo("Title 3");
    }

    @Test
    public void testAddDescription() {
        MediaObject mediaObject = new Program();
        mediaObject.addDescription(null);
        assertThat(mediaObject.getDescriptions()).isEmpty();

        mediaObject.addDescription(new Description("Des 1", OwnerType.MIS, TextualType.MAIN));
        assertThat(mediaObject.getDescriptions().first().getParent()).isEqualTo(mediaObject);
        assertThat(mediaObject.getDescriptions()).hasSize(1);
        assertThat(mediaObject.getDescriptions().last().getDescription()).isEqualTo("Des 1");

        mediaObject.addDescription(new Description("Des 2", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().getDescription()).isEqualTo("Des 2");

        mediaObject.addDescription(new Description("Des 3", OwnerType.MIS, TextualType.EPISODE));
        assertThat(mediaObject.getDescriptions()).hasSize(2);
        assertThat(mediaObject.getDescriptions().last().getDescription()).isEqualTo("Des 3");
    }

    @Test
    public void testGetAncestors() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1);
        group1.createMemberOf(group2, 1);
        group2.createMemberOf(root, 1);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test
    public void testGetAncestorsForUniqueReferences() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        Group group2 = new Group(GroupType.PLAYLIST);
        Group root = new Group(GroupType.PLAYLIST);

        program.createMemberOf(group1, 1);
        program.createMemberOf(group2, 2);
        group1.createMemberOf(root, 1);
        group2.createMemberOf(root, 2);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3); // somebody thought this should be 4?
    }

    @Test
    public void testGetAncestorsForUniqueReferencesWithId() throws CircularReferenceException {
        Program program = new Program();
        Group group1 = new Group(GroupType.PLAYLIST);
        group1.setId(1L);
        Group group2 = new Group(GroupType.PLAYLIST);
        group2.setId(2L);
        Group root = new Group(GroupType.PLAYLIST);
        root.setId(3L);

        program.createMemberOf(group1, 1);
        program.createMemberOf(group2, 2);
        group1.createMemberOf(root, 1);
        group2.createMemberOf(root, 2);

        SortedSet<MediaObject> ancestors = program.getAncestors();

        assertThat(ancestors).hasSize(3);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfForSelf() throws CircularReferenceException {
        Group g1 = new Group();

        g1.createMemberOf(g1, 1);
    }

    @Test(expected = CircularReferenceException.class)
    public void testCreateMemberOfForCircularity() throws CircularReferenceException {
        Group g1 = new Group(GroupType.PLAYLIST);
        Group g2 = new Group(GroupType.PLAYLIST);
        Group g3 = new Group(GroupType.PLAYLIST);
        Group g4 = new Group(GroupType.PLAYLIST);

        g1.createMemberOf(g2, 1);
        g2.createMemberOf(g3, 1);
        g3.createMemberOf(g4, 1);
        g4.createMemberOf(g1, 1);

        //assertThat(g1.getAncestors()).hasSize(4);
    }

    protected Program getTestProgram() throws CircularReferenceException {
        Program program = new Program(1L);
        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD)));
        program.setUrn("urn:vpro:media:program:123");
        program.setCreationInstant(Instant.EPOCH);
        Title t = new Title("bla", OwnerType.BROADCASTER, TextualType.MAIN);
        program.addTitle(t);
        program.addDescription("bloe", OwnerType.BROADCASTER, TextualType.MAIN);

        Group group = new Group();
        group.setUrn("urn:vpro:media:group:122");
        program.addBroadcaster(new Broadcaster("VPRO", "V.P.R.O"));
        MemberRef ref = group.createMember(program, 1);
        ref.setHighlighted(true);
        ref.setAdded(Instant.EPOCH);

        return program;
    }

    @Test
    public void program() throws CircularReferenceException {
        Program p1 = new Program();
        assertThat(p1.getMemberOf()).isEmpty();
        Program program = getTestProgram();
        assertThat(program.getMemberOf()).hasSize(1);
    }

    @Test
    public void testProgramValidation() throws Exception {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void testMidValidation() throws Exception {
        Program p = new Program();
        p.setType(ProgramType.BROADCAST);
        p.setAVType(AVType.MIXED);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setMid("foo/bar");
        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
        assertThat(constraintViolations.iterator().next().getMessageTemplate()).isEqualTo("{nl.vpro.constraints.mid}");
    }


    @Test
    public void testRelationValidation() throws Exception {
        Relation r = new Relation(new RelationDefinition("AAAA", "a", "a"));
        r.setUriRef(":");
        Program p = new Program(AVType.AUDIO, ProgramType.BROADCAST);
        p.addTitle("title", OwnerType.BROADCASTER, TextualType.MAIN);
        p.setType(ProgramType.CLIP);
        p.addRelation(r);

        Set<ConstraintViolation<Program>> constraintViolations = validator.validate(p);
        assertThat(constraintViolations).hasSize(1);
    }

    @Test
    public void sortDate() {
        Program program = new Program();
        assertThat(Math.abs(program.getSortInstant().toEpochMilli() - System.currentTimeMillis())).isLessThan(10000);
        Instant publishDate = Instant.ofEpochMilli(1344043500362L);
        program.setPublishStartInstant(publishDate);
        assertThat(program.getSortInstant()).isEqualTo(publishDate);
        ScheduleEvent se = new ScheduleEvent();
        se.setStartInstant(Instant.ofEpochMilli(1444043500362L));
        program.addScheduleEvent(se);
        assertThat(program.getSortInstant()).isEqualTo(se.getStartInstant());
        Segment segment = new Segment();
        program.addSegment(segment);
        assertThat(segment.getSortInstant()).isEqualTo(se.getStartInstant());
    }

    @Test
    public void testAddLocationOnDuplicates() {
        Location l1 = new Location("TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("TEST_URL", OwnerType.NEBO);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddLocationOnDuplicatesCollisions() {
        Location l1 = new Location("TEST_URL", OwnerType.NEBO);
        l1.setAvAttributes(new AVAttributes(100000, AVFileFormat.WM));

        Location l2 = new Location("TEST_URL", OwnerType.MIS);
        l2.setAvAttributes(new AVAttributes(110000, AVFileFormat.H264));

        Program p = MediaBuilder.program().build();
        p.addLocation(l1);
        p.addLocation(l2);

        Assertions.assertThat(p.getLocations()).hasSize(1);
        Assertions.assertThat(p.getLocations().first().getBitrate()).isEqualTo(110000);
    }

    @Test
    public void testAddTwoLocationsWithSameAuthorityRecords() throws Exception {
        Program program = new Program(1L);


        Location l1 = new Location("aaa", OwnerType.BROADCASTER);
        Location l2 = new Location("bbb", OwnerType.BROADCASTER);

        program.addLocation(l1);
        program.addLocation(l2);

        l1.setPlatform(Platform.INTERNETVOD);
        l2.setPlatform(Platform.INTERNETVOD);



        assertThat(program.getLocations()).hasSize(2);

        Prediction record = program.getPrediction(Platform.INTERNETVOD);
        assertThat(record).isNotNull();
        assertThat(record).isSameAs(program.getLocations().first().getAuthorityRecord());
        assertThat(record).isSameAs(program.getLocations().last().getAuthorityRecord());
    }


    @Test
    public void testAddLocationsOnlyUpdateCeresPredictions() throws Exception {
        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        Program target = new Program(1L);


        target.addLocation(l1);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNull();
    }

    @Test
    public void testAddLocationsOnlyUpdatePlatformPredictions() throws Exception {
        Program target = new Program(1L);
        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        target.addLocation(l1);

        l1.setPlatform(Platform.PLUSVOD);

        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus.getPlatform()).isEqualTo(Platform.PLUSVOD); // used to be 'isNull' but I don't understand that.
    }

    @Test
    // MSE-2313
    public void testSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa.a/a", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.INTERNETVOD);

        Program program = new Program(1L);
        program.setWorkflow(Workflow.PUBLISHED);


        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getWorkflow()).isEqualTo(Workflow.FOR_REPUBLICATION);

    }

    @Test
    // MSE-2313
    public void testDontSilentlyFixStateOfPredictionIfLocationsAndOnlyAnnounced() {
        Location l1 = new Location("http://aaa", OwnerType.BROADCASTER);
        l1.setPlatform(Platform.PLUSVOD);

        Program program = new Program(1L);


        program.addLocation(l1);

        program.setPredictions(Arrays.asList(new Prediction(Platform.INTERNETVOD, Prediction.State.ANNOUNCED)));

        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.ANNOUNCED);
    }


    @Test
    public void testAddLocationsOnPredictionUpdate() throws Exception {
        Program target = new Program(1L);
        target.findOrCreatePrediction(Platform.PLUSVOD);

        Location l1 = new Location("aaa", OwnerType.BROADCASTER);

        target.addLocation(l1);

        l1.setPlatform(Platform.PLUSVOD);
        l1.setPublishStartInstant(Instant.ofEpochMilli(5));
        l1.setPublishStopInstant(Instant.ofEpochMilli(10));




        Prediction plus = target.getPrediction(Platform.PLUSVOD);
        assertThat(plus).isNotNull();
        assertThat(plus.getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(plus.getPublishStartInstant()).isEqualTo(Instant.ofEpochMilli(5));
        assertThat(plus.getPublishStopInstant()).isEqualTo(Instant.ofEpochMilli(10));
    }

    @Test
    public void testSortDateWithScheduleEvents() throws Exception {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .scheduleEvents(
                new ScheduleEvent(Channel.NED2, Instant.ofEpochMilli(13), java.time.Duration.ofMillis(10)),
                new ScheduleEvent(Channel.NED1, Instant.ofEpochMilli(3), java.time.Duration.ofMillis(10))
            )
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(3));
    }

    @Test
    public void testSortDateWithPublishStart() throws Exception {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .publishStart(Instant.ofEpochMilli(2))
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(2));
    }

    @Test
    public void testSortDateWithCreationDate() throws Exception {
        final Program program = MediaBuilder.program()
            .creationInstant(Instant.ofEpochMilli(1))
            .build();

        assertThat(program.getSortInstant()).isEqualTo(Instant.ofEpochMilli(1));
    }



    @Test
    public void testRealizePrediction() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();


        final Location location1 = new Location("http://bla/1", OwnerType.BROADCASTER);
        location1.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location1);

        program.realizePrediction(location1);

        final Location location2 = new Location("http://bla/2", OwnerType.BROADCASTER);
        location2.setPlatform(Platform.INTERNETVOD);
        program.addLocation(location2);




        assertThat(program.getPrediction(Platform.INTERNETVOD).getState()).isEqualTo(Prediction.State.REALIZED);
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStartInstant()).isNull();
        assertThat(program.getPrediction(Platform.INTERNETVOD).getPublishStopInstant()).isNull();


    }

    @Test
    public void testUnmarshal() {
        final Program program = MediaBuilder.program()
            .id(1L)
            .build();

        program.findOrCreatePrediction(Platform.INTERNETVOD).setState(Prediction.State.ANNOUNCED);

        Program result = JAXBTestUtil.roundTrip(program);

        assertThat(result.getPrediction(Platform.INTERNETVOD)).isNotNull();
    }

    @Test
    public void testHash() {
        final Program program = MediaBuilder.program()
            .lastModified(Instant.now())
            .creationInstant(Instant.ofEpochMilli(10000))
            .lastPublished(Instant.now())
            .id(1L)
            .build();
        program.acceptChanges();
        assertThat(program.getHash()).isEqualTo(3754246039L);
    }


    @Test
    public void testHasChanges() {
        final Program program = MediaBuilder.program()
            .lastModified(Instant.now())
            .lastPublished(Instant.now())
            .id(1L)
            .build();

        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
        program.setPublishStartInstant(Instant.now());
        assertThat(program.hasChanges()).isTrue();
        program.acceptChanges();
        assertThat(program.hasChanges()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetWorkflowWhenMerged() throws Exception {
        final Program merged = new Program();

        merged.setMergedTo(new Group());
        merged.setWorkflow(Workflow.PUBLISHED);
    }

    @Test
    public void testFindAncestry() throws Exception {
        final Group grandParent = MediaBuilder.group().titles(new Title("Grand parent", OwnerType.BROADCASTER, TextualType.MAIN)).build();
        final Program parent = MediaBuilder.program().titles(new Title("Parent", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(grandParent, 1).build();
        final Program child = MediaBuilder.program().titles(new Title("Child", OwnerType.BROADCASTER, TextualType.MAIN)).memberOf(parent, 1).build();

        final List<MediaObject> ancestry = child.findAncestry(grandParent);
        assertThat(ancestry).hasSize(2);
        assertThat(ancestry.get(0)).isSameAs(grandParent);
        assertThat(ancestry.get(1)).isSameAs(parent);
    }

    @Test
    public void testAddImageWithMultipleOwners() {
        Image imgn1 = Image.builder().imageUri("urn:image:1").owner(NEBO).build();
        Image imgn2 = Image.builder().imageUri("urn:image:2").owner(NEBO).build();
        Image imgn3 = Image.builder().imageUri("urn:image:3").owner(NEBO).build();

        Image imgc1 = Image.builder().imageUri("urn:image:ceres1").owner(CERES).build();
        Image imgc2 = Image.builder().imageUri("urn:image:ceres2").owner(CERES).build();
        Image imgc3 = Image.builder().imageUri("urn:image:ceres3").owner(CERES).build();

        Program existing = MediaBuilder.program().images(
            imgn1, imgn2, //nebo
            imgc1, imgc2, imgc3 // ceres
        ).build();


        // incoming has ceres images too, this is odd, but they will be ignored
        Program incoming = MediaBuilder.program().images(
            imgn3, imgn1, imgn2, // nebo, added one and ordered
            imgc1, imgc3 // other images should be ignored
        ).build();

        existing.mergeImages(incoming, NEBO); // because we are nebo, and should not have shipped any thing different

        // arrived and in correct order
        assertEquals("urn:image:3", existing.getImages().get(0).getImageUri());
        assertEquals("urn:image:1", existing.getImages().get(1).getImageUri());
        assertEquals("urn:image:2", existing.getImages().get(2).getImageUri());

        // other images remain untouched and in same  same order
        assertEquals("urn:image:ceres1", existing.getImages().get(3).getImageUri());
        assertEquals("urn:image:ceres2", existing.getImages().get(4).getImageUri());
        assertEquals("urn:image:ceres3", existing.getImages().get(5).getImageUri());
    }
}
