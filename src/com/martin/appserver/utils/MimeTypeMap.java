package com.martin.appserver.utils;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeMap {
    private static final MimeTypeMap INSTANCE = new MimeTypeMap();
    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        mimeTypes.put("html", "text/html");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("zip", "application/zip");
    }

    private MimeTypeMap() {
    }

    public static MimeTypeMap getSingleton() {
        return INSTANCE;
    }

    public static String getFileExtensionFromUrl(String url) {
        if (url == null)
            return "";
        int dot = url.lastIndexOf('.');
        if (dot >= 0) {
            return url.substring(dot + 1).toLowerCase();
        }
        return "";
    }

    public String getMimeTypeFromExtension(String extension) {
        return mimeTypes.get(extension);
    }
}
