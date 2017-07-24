/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */

package nl.vpro.domain.image;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.AbstractPublishableObject;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.support.License;
import nl.vpro.validation.WarningValidatorGroup;

@Entity
@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "imageType",
    propOrder = {
        "title",
        "description",
        "height",
        "width",
        "heightInMm",
        "widthInMm",
        "mimeType",
        "size",
        "downloadUrl",
        "etag",
        "source",
        "sourceName",
        "license",
        "data"}
        )
public class Image extends AbstractPublishableObject<Image> implements ImageMetadata<Image> {


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="imageType")
    @XmlAttribute
    private ImageType type;

    @Enumerated(EnumType.STRING)
    @XmlTransient
    private ImageFormat imageFormat;

    @Column(nullable = false)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    private String title;

    @Column
    private String description;

    private Integer height;

    private Integer width;

    @XmlElement(name = "heightMm")
    private Float heightInMm;

    @XmlElement(name = "widthMm")
    private Float widthInMm;

    private Long size;

    @Column(columnDefinition = "TEXT", unique = true, length = 1024)
    private URI downloadUrl;

    private String etag;

    @XmlTransient
    private byte[] hash;

    @XmlElement(namespace = Xmlns.SHARED_NAMESPACE)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {WarningValidatorGroup.class})
    @Getter
    @Setter
    private License license;

    @Getter
    @Setter
    private String source;

    @Getter
    @Setter
    private String sourceName;

    @Lob
    @XmlTransient
    private Blob data;

    @Transient
    @XmlTransient
    private InputStream cachedInputStream;

    public Image() {
    }

    public Image(String title) {
        setTitle(title);
    }


    @Override
    public ImageType getType() {
        return type;
    }

    @Override
    public void setType(ImageType imageType) {
        this.type = imageType;
    }


    @Override
    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    @Override
    public Image setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        if(title == null || title.length() < 255) {
            this.title = title;
        } else {
            this.title = title.substring(255);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        if(description == null || description.length() < 255) {
            this.description = description;
        } else {
            this.description = description.substring(255);
        }
    }

    @Override
    @XmlElement
    public String getMimeType() {
        if(imageFormat == null) {
            return null;
        }
        return imageFormat.getMimeType();
    }


    @Override
    public Integer getHeight() {
        return height;
    }

    @Override
    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public void setDate(String date) {
        System.out.println("--");

    }


    @Override
    public Float getHeightInMm() {
        return heightInMm;
    }

    @Override
    public Image setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }


    @Override
    public Float getWidthInMm() {
        return widthInMm;
    }

    @Override
    public Image setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }
    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public Image setSize(Long size) {
        this.size = size;
        return this;
    }

    public String getSizeFormatted() {
        if (size == null){
            return null;
        }
        float result;
        String unit;

        // MM: BTW, these units are incorrect.
        // In S.I. the prefixes T, M and k are powers of 10.
        // Correct would be useage of prefixes Ti, Mi and Ki.
        if(size > 1024 * 1024 * 1024) {
            result = size / 1024 * 1024 * 1024;
            unit = "TB";
        } else if(size > 1024 * 1024) {
            result = size / 1024 * 1024;
            unit = "MB";
        } else {
            result = size / 1024;
            unit = "kB";
        }

        return String.format("%1$.1f %2$s", result, unit);
    }

    @XmlTransient
    public Blob getBlob() {
        return data;
    }

    public Image setBlob(Blob data) {
        this.data = data;
        return this;
    }

    @XmlElement(name = "data")
    @XmlMimeType("application/octet-stream")
    public DataHandler getData() {
        return new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                if(cachedInputStream == null) {
                    try {
                        return data.getBinaryStream();
                    } catch(SQLException e) {
                        throw new IOException(e);
                    }
                }

                return cachedInputStream;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                try {
                    return data.setBinaryStream(1);
                } catch(SQLException e) {
                    throw new IOException(e);
                }
            }

            @Override
            public String getContentType() {
                return getMimeType();
            }

            @Override
            public String getName() {
                return title;
            }
        });
    }


    void setCachedInputStream(InputStream cachedInputStream) {
        this.cachedInputStream = cachedInputStream;
    }

    @Override
    protected String getUrnPrefix() {
        return "urn:vpro:image:";
    }

    @Override
    public URI getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public Image setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public byte[] getHash() {
        return hash;
    }

    protected void setHash(byte[] hash) {
        this.hash = hash;
    }

    @Override
    public String getEtag() {
        return etag;
    }

    @Override
    public String getCredits() {
        return null;

    }

    @Override
    public String getDate() {
        return null;
    }



    @Override
    public Image setEtag(String etag) {
        this.etag = etag;
        return this;
    }

}
