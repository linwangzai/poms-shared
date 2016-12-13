/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.License;
import nl.vpro.domain.page.Image;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;
import nl.vpro.validation.WarningValidatorGroup;


@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageUpdateType", propOrder = {
    "title",
    "description",
    "image",
    "source",
    "sourceName",
    "license",
    "credits"
})
@Builder
public class ImageUpdate {

    @XmlAttribute(required = true)
    @NotNull
    private ImageType type;

    @XmlElement
	@Size.List({
			@Size(max = 512, message = "image title contains too many (> {max}) characters"),
			@Size(min = 1, message = "image title contains no characters"),
	})
    @NoHtml
    private String title;

    @XmlElement
    @NoHtml
    private String description;


    @NoHtml
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String credits;

    @URI
    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private String source;

    @XmlElement
    @Size.List({
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @NotNull(groups = {WarningValidatorGroup.class})
    private String sourceName;

    @XmlElement
    @NotNull(groups = {WarningValidatorGroup.class})
    private License license;


    @XmlElements(value = {
//        @XmlElement(name = "imageData", namespace = Xmlns.UPDATE_NAMESPACE, type = ImageData.class),
//        @XmlElement(name = "urn", namespace = Xmlns.UPDATE_NAMESPACE, type = String.class),
        @XmlElement(name = "imageLocation", type = ImageLocation.class)
    })
    @NotNull
    @Valid
    private Object image;

    public ImageUpdate() {
    }

/*
    public ImageUpdate(ImageType type, String title, String description, ImageData image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }
*/

    public ImageUpdate(ImageType type, String title, String description, ImageLocation image) {
        this.description = description;
        this.title = title;
        this.type = type;
        this.image = image;
    }

    public ImageUpdate(Image image) {
        type = image.getType();
        title = image.getTitle();
        description = image.getDescription();
        this.image = new ImageLocation(image.getUrl());
    }

    public Image toImage() {
        Image result = new Image();
        result.setType(type);
        result.setTitle(title);
        result.setDescription(description);
        result.setSource(source);
        result.setSourceName(sourceName);
        result.setCredits(credits);
        result.setLicense(license);
        if(image instanceof ImageLocation) {
            result.setUrl(((ImageLocation)image).getUrl());
        } else {
            throw new UnsupportedOperationException("We only support image locations for now");
        }

        return result;
    }

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(ImageLocation image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ImageUpdate)) {
            return false;
        }

        ImageUpdate that = (ImageUpdate)o;

        return image.equals(that.image);
    }

    @Override
    public int hashCode() {
        return image.hashCode();
    }
}
