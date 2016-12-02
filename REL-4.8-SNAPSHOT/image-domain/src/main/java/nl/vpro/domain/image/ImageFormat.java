package nl.vpro.domain.image;

public enum ImageFormat {
    BMP("image/bmp", "bmp"),
    GIF("image/gif", "gif"),
    IEF("image/ief", "ief"),
    IFF("image/iff", "iff"),
    JPG("image/jpeg", "jpe", "jpeg", "jpg"),
    JFIF("image/pipeg", "jfif"),
    PNG("image/png", "png"),
    PBM("image/x-portable-bitmap", "pbm"),
    PGM("image/x-portable-graymap", "pgm"),
    PNM("image/x-portable-anymap", "pnm"),
    PPM("image/x-portable-pixmap", "ppm"),
    SVG("image/svg+xml", "svg"), // Expensive!
    RAS("image/x-cmu-raster", "ras"),
    RGB("image/x-rgb", "rgb"),
    TIF("image/tiff", "tif", "tiff"),
    XBM("image/x-xbitmap", "xbm"),
    XPM("image/x-xpixmap", "xpm");

    private String mimeType;

    private String[] extensions;

    private ImageFormat(String mimeType, String... extensions) {
        this.mimeType = mimeType;
        this.extensions = extensions;
    }

    public static ImageFormat forFileExtension(String extension) throws UnsupportedImageFormatException {
        for(ImageFormat type : ImageFormat.values()) {
            for(String match : type.extensions) {
                if(match.equals(extension.toLowerCase().trim())) {
                    return type;
                }
            }
        }

        throw new UnsupportedImageFormatException("No matching type for file extension: " + extension);
    }

    public static ImageFormat forMimeType(String mimeType) throws UnsupportedImageFormatException {
        for(ImageFormat type : ImageFormat.values()) {
            if(type.getMimeType().equals(mimeType.toLowerCase().trim())) {
                return type;
            }
        }

        throw new UnsupportedImageFormatException("No matching type for mime-type: " + mimeType);
    }

    public String getFileExtension() {
        return name().toLowerCase();
    }

    public String getMimeType() {
        return mimeType;
    }
}
