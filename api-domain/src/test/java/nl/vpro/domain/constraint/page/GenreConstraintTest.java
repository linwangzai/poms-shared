/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.vpro.domain.classification.CachedURLClassificationServiceImpl;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageBuilder;
import nl.vpro.domain.page.PageType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class GenreConstraintTest {
    
    private static CachedURLClassificationServiceImpl cs;

    @BeforeClass
    public static void init() throws URISyntaxException {
        URL url = GenreConstraintTest.class.getResource("/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        cs = new CachedURLClassificationServiceImpl(url.toURI());
    }
    
    @Test
    public void testGetValue() throws Exception {
        GenreConstraint in = new GenreConstraint("jeugd");
        GenreConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:genreConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">jeugd</local:genreConstraint>");
        assertThat(out.getValue()).isEqualTo("jeugd");
    }

    @Test
    public void testGetESPath() throws Exception {
        assertThat(new GenreConstraint().getESPath()).isEqualTo("genres.id");
    }

    @Test
    public void testApplyWhenTrue() throws Exception {
        Page article = PageBuilder.page(PageType.ARTICLE).genres(cs.getTerm("3.0.1.1")).build();
        assertThat(new GenreConstraint("3.0.1.1").test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() throws Exception {
        Page article = PageBuilder.page(PageType.ARTICLE).genres(cs.getTerm("3.0.1.1.7")).build();
        assertThat(new GenreConstraint("3.0.1.1.7").test(article)).isTrue();
        assertThat(new GenreConstraint("3.0.1.1").test(article)).isFalse();
        assertThat(new GenreConstraint("3.0.3").test(article)).isFalse();
    }
    
    @Test
    public void testSubPath() throws Exception {
        GenreConstraint genreConstraint = new GenreConstraint("3.0.1.*");
        assertThat(genreConstraint.test(article("3.0.1.1.7"))).isTrue();
        assertThat(genreConstraint.test(article("3.0.1.1"))).isTrue();
        assertThat(genreConstraint.test(article("3.0.1"))).isTrue();
        assertThat(new GenreConstraint("3.0.1*").test(article("3.0.1"))).isTrue();
    }

    private Page article(String g) {
        Page article = PageBuilder.page(PageType.ARTICLE).genres(cs.getTerm(g)).build();
        return article;
    }
}
