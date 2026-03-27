// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.utils;

import java.util.HashMap;
import java.util.Map;

public class QueryParser {
   public QueryParser() {
   }

   public static Map<String, String> parse(String path) {
      Map<String, String> params = new HashMap();
      int questionMarkIndex = path.indexOf('?');
      if (questionMarkIndex == -1) {
         return params;
      } else {
         String queryString = path.substring(questionMarkIndex + 1);
         String[] pairs = queryString.split("&");
         String[] var5 = pairs;
         int var6 = pairs.length;

         for (int var7 = 0; var7 < var6; ++var7) {
            String pair = var5[var7];
            int equalsIndex = pair.indexOf('=');
            if (equalsIndex != -1) {
               String key = pair.substring(0, equalsIndex);
               String value = pair.substring(equalsIndex + 1);
               params.put(key, value);
            }
         }

         return params;
      }
   }

   public static String getPathWithoutQuery(String path) {
      int questionMarkIndex = path.indexOf(63);
      return questionMarkIndex == -1 ? path : path.substring(0, questionMarkIndex);
   }
}
