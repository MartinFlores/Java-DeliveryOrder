// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonResponse {
   private Map<String, Object> data = new HashMap();

   public static String success(String message) {
      return create().put("status", "success").put("message", message).send();
   }

   public static String error(String message) {
      return create().put("status", "error").put("message", message).send();
   }

   private JsonResponse() {
   }

   public static JsonResponse create() {
      return new JsonResponse();
   }

   public JsonResponse put(String key, Object value) {
      this.data.put(key, value);
      return this;
   }

   public String send() {
      return this.formatValue(this.data);
   }

   private String formatValue(Object value) {
      if (value == null) {
         return "null";
      } else if (value instanceof String) {
         return "\"" + value.toString().replace("\"", "\\\"") + "\"";
      } else if (!(value instanceof Number) && !(value instanceof Boolean)) {
         Iterator var5;
         StringBuilder sb;
         boolean first;
         if (value instanceof Map) {
            Map<?, ?> map = (Map) value;
            sb = new StringBuilder("{");
            first = true;

            for (var5 = map.entrySet().iterator(); var5.hasNext(); first = false) {
               Map.Entry<?, ?> entry = (Map.Entry) var5.next();
               if (!first) {
                  sb.append(",");
               }

               sb.append("\"").append(entry.getKey()).append("\":").append(this.formatValue(entry.getValue()));
            }

            sb.append("}");
            return sb.toString();
         } else if (value instanceof Collection) {
            Collection<?> col = (Collection) value;
            sb = new StringBuilder("[");
            first = true;

            for (var5 = col.iterator(); var5.hasNext(); first = false) {
               Object obj = var5.next();
               if (!first) {
                  sb.append(",");
               }

               sb.append(this.formatValue(obj));
            }

            sb.append("]");
            return sb.toString();
         } else if (value.getClass().isArray()) {
            sb = new StringBuilder("[");
            int length = Array.getLength(value);

            for (int i = 0; i < length; ++i) {
               if (i > 0) {
                  sb.append(",");
               }

               sb.append(this.formatValue(Array.get(value, i)));
            }

            sb.append("]");
            return sb.toString();
         } else {
            return "\"" + value.toString().replace("\"", "\\\"") + "\"";
         }
      } else {
         return value.toString();
      }
   }
}
