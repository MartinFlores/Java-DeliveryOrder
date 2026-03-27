// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
   public ZipUtils() {
   }

   public static void unzipAssets(InputStream inputStream, File targetDir) {
      try {
         ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream));

         ZipEntry ze;
         for (byte[] buffer = new byte[1024]; (ze = zis.getNextEntry()) != null; zis.closeEntry()) {
            File f = new File(targetDir, ze.getName());
            if (ze.isDirectory()) {
               f.mkdirs();
            } else {
               f.getParentFile().mkdirs();
               FileOutputStream fos = new FileOutputStream(f);

               int count;
               while ((count = zis.read(buffer)) != -1) {
                  fos.write(buffer, 0, count);
               }

               fos.close();
            }
         }

         zis.close();
      } catch (Exception var8) {
         Log.e("AppServer", "Error al descomprimir ZIP: " + var8.getMessage());
      }

   }
}
