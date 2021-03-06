/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.6
 */
@Deprecated
public class ImagesTest {
    @Before
    public void init() {
        System.clearProperty(Images.IMAGE_SERVER_BASE_URL_PROPERTY);
        ImageUrlServiceHolder.setInstance(() -> System.getProperty(Images.IMAGE_SERVER_BASE_URL_PROPERTY));
    }

    @Test
    public void testGetImageLocationOnMissingSystemProperty() {
        String location = Images.getImageLocation(new Image(), null);
        assertThat(location).isNull();
    }

    @Test(expected = NullPointerException.class)
    public void testGetImageLocationOnNullArgument() {
        System.setProperty("image.server.baseUrl", "http://domain.com/");
        Images.getImageLocation(null, null);
    }

    @Test(expected = NullPointerException.class)
    @Ignore
    public void testGetImageLocationOnEmptyURI() {
        System.setProperty("image.server.baseUrl", "http://domain.com/");
        Images.getImageLocation(new Image(), "jpg");
    }

    @Test
    public void testGetImageLocationOnNullExtension() {
        System.setProperty("image.server.baseUrl", "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), null);
        assertThat(location).isEqualTo("http://domain.com/12345");
    }

    @Test
    public void testGetImageLocationOnInValidURI() {
        System.setProperty("image.server.baseUrl", "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:123aa"), "jpg");
        assertThat(location).isNull();
    }

    @Test
    public void testGetImageLocationWhenValid() {
        System.setProperty("image.server.baseUrl", "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), "jpg");
        assertThat(location).isEqualTo("http://domain.com/12345.jpg");
    }

    @Test
    public void testGetImageLocationWithConversion() {
        System.setProperty("image.server.baseUrl", "http://domain.com/");
        String location = Images.getImageLocation(new Image(OwnerType.BROADCASTER, "urn:vpro:image:12345"), "jpg", "s350");
        assertThat(location).isEqualTo("http://domain.com/s350/12345.jpg");
    }
}
