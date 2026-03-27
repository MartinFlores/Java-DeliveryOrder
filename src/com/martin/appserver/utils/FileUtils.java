// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.utils;

import com.martin.appserver.utils.MimeTypeMap;
import java.io.File;

public class FileUtils {
   public FileUtils() {
   }

   public static String normalizePath(String path) {
      if (path.equals("/") || path.equals("/index.html")) return "/index.html";
      if (path.startsWith("/app")) return path.substring(4).isEmpty() ? "/index.html" : path.substring(4);
      return path;
   }

   public static String cleanPath(String path) {
      return path.startsWith("/") ? path.substring(1) : path;
   }

   public static String getMimeType(String filePath) {
      String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
      String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
      return mimeType != null ? mimeType : "application/octet-stream";
   }

   public static boolean isValidFile(File file) {
      return file.exists() && !file.isDirectory();
   }
}
