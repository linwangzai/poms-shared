package nl.vpro.domain.media;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.theory.ComparableTest;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberRefTest extends ComparableTest<MemberRef> {

    private static Program member = new Program(1l);

    private static Group owner = new Group(2l);

    @DataPoint
    public static MemberRef nullArgument = null;

    @DataPoint
    public static MemberRef nullFields = new MemberRef();

    @DataPoint
    public static MemberRef midReference = memberRefWithMid();

    @DataPoint
    public static MemberRef urnReference = memberRefWithUrn();

    @DataPoint
    public static MemberRef cridReference = memberRefWithCrid();

    @DataPoint
    public static MemberRef duplicateA = new MemberRef(member, owner, 1);

    @DataPoint
    public static MemberRef duplicateB = new MemberRef(member, owner, 1);

    @DataPoint
    public static MemberRef duplicateWithHigherPosition = new MemberRef(member, owner, 2);

    @DataPoint
    public static MemberRef duplicateWithId10 = new MemberRef(10l, member, owner, 1);

    @DataPoint
    public static MemberRef duplicateWithId20 = new MemberRef(20l, member, owner, 1);

    @Test
    public void testEqualsOnDuplicates() throws Exception {
        assertThat(duplicateA).isEqualTo(duplicateB);
    }

    @Test
    public void testCompareToOnDuplicates() throws Exception {
        assertThat(duplicateA.compareTo(duplicateB)).isEqualTo(0);
    }

    @Test
    public void testEqualsOnDuplicateWithOtherNumber() throws Exception {
        assertThat(duplicateA).isNotEqualTo(duplicateWithHigherPosition);
    }

    @Test
    public void testCompareToOnDuplicateWithOtherNumber() throws Exception {
        assertThat(duplicateA.compareTo(duplicateWithHigherPosition)).isLessThan(0);
    }

    @Test
    public void testEqualsIgnoreId() throws Exception {
        assertThat(duplicateWithId10).isEqualTo(duplicateWithId20);
    }

    @Test
    public void testCompareToIgnoreId() throws Exception {
        assertThat(duplicateWithId10.compareTo(duplicateWithId20)).isEqualTo(0);
    }

    @Test
    public void testTypeRoundTrip() throws Exception {
        MemberRef memberRef = new MemberRef();
        memberRef.setType(MediaType.CLIP);
        MemberRef result = JAXBTestUtil.roundTrip(memberRef, "type=\"CLIP\"");
        assertThat(result.getType()).isEqualTo(MediaType.CLIP);
    }

    private static MemberRef memberRefWithMid() {
        MemberRef memberRef = new MemberRef();
        memberRef.setMember(member);
        memberRef.setMidRef("VPROWON_12345");
        return memberRef;
    }

    private static MemberRef memberRefWithUrn() {
        MemberRef memberRef = new MemberRef();
        memberRef.setMember(member);
        memberRef.setUrnRef("urn:12345");
        return memberRef;
    }

    private static MemberRef memberRefWithCrid() {
        MemberRef memberRef = new MemberRef();
        memberRef.setMember(member);
        memberRef.setCridRef("crid://somedomain");
        return memberRef;
    }
}
