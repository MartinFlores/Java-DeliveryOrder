// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ServerLogger {
   private static final int MAX_LOGS = 100;
   private static final List<String> logs = new ArrayList();

   public ServerLogger() {
   }

   public static void log(String message) {
      synchronized(logs) {
         if (logs.size() >= 100) {
            logs.remove(0);
         }

         String timestamp = (new SimpleDateFormat("HH:mm:ss")).format(new Date());
         logs.add("[" + timestamp + "] " + message);
      }
   }

   public static String getLogs() {
      synchronized(logs) {
         StringBuilder sb = new StringBuilder();
         Iterator var2 = logs.iterator();

         while(var2.hasNext()) {
            String log = (String)var2.next();
            sb.append(log).append("\n");
         }

         return sb.toString();
      }
   }

   public static String getLogsJson() {
      synchronized(logs) {
         StringBuilder json = new StringBuilder("[");

         for(int i = 0; i < logs.size(); ++i) {
            if (i > 0) {
               json.append(",");
            }

            json.append("\"").append(((String)logs.get(i)).replace("\"", "\\\"")).append("\"");
         }

         json.append("]");
         return json.toString();
      }
   }

   public static void clear() {
      synchronized(logs) {
         logs.clear();
      }
   }
}
