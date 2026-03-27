// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.routing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class RouteScanner {
   private static final Gson gson = (new GsonBuilder()).create();

   public RouteScanner() {
   }

   public static void scan(Object controller) {
      Class<?> clazz = controller.getClass();
      String prefix = "";
      if (clazz.isAnnotationPresent(Controller.class)) {
         prefix = ((Controller)clazz.getAnnotation(Controller.class)).value();
      }

      Method[] var3 = clazz.getDeclaredMethods();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method method = var3[var5];
         if (method.isAnnotationPresent(Get.class)) {
            registerMethod("GET", prefix, method, controller);
         }

         if (method.isAnnotationPresent(Post.class)) {
            registerMethod("POST", prefix, method, controller);
         }

         if (method.isAnnotationPresent(Put.class)) {
            registerMethod("PUT", prefix, method, controller);
         }

         if (method.isAnnotationPresent(Delete.class)) {
            registerMethod("DELETE", prefix, method, controller);
         }
      }

   }

   private static void registerMethod(String httpMethod, String prefix, final Method method, final Object controller) {
      String subPath = "";
      if (httpMethod.equals("GET")) {
         subPath = ((Get)method.getAnnotation(Get.class)).value();
      }

      if (httpMethod.equals("POST")) {
         subPath = ((Post)method.getAnnotation(Post.class)).value();
      }

      if (httpMethod.equals("PUT")) {
         subPath = ((Put)method.getAnnotation(Put.class)).value();
      }

      if (httpMethod.equals("DELETE")) {
         subPath = ((Delete)method.getAnnotation(Delete.class)).value();
      }

      String fullPath = formatPath(prefix, subPath);
      RouteHandler handler = new RouteHandler() {
         public Object handle(Map<String, String> queryParams, String body) {
            try {
               Parameter[] params = method.getParameters();
               Object[] args = new Object[params.length];

               for(int i = 0; i < params.length; ++i) {
                  if (params[i].isAnnotationPresent(Body.class)) {
                     String cleanBody = body.replace("\u0000", "").trim();
                     JsonReader reader = new JsonReader(new StringReader(cleanBody));
                     reader.setLenient(true);
                     args[i] = RouteScanner.gson.fromJson(reader, params[i].getType());
                  } else if (params[i].getType().equals(Map.class)) {
                     args[i] = queryParams;
                  } else if (params[i].getType().equals(String.class)) {
                     args[i] = body;
                  }
               }

               return method.invoke(controller, args);
            } catch (Exception var8) {
               var8.printStackTrace();
               return "{\"error\":\"Invoke Error: " + var8.getMessage() + "\"}";
            }
         }
      };
      if (httpMethod.equals("GET")) {
         Router.get(fullPath, handler);
      }

      if (httpMethod.equals("POST")) {
         Router.post(fullPath, handler);
      }

      if (httpMethod.equals("PUT")) {
         Router.put(fullPath, handler);
      }

      if (httpMethod.equals("DELETE")) {
         Router.delete(fullPath, handler);
      }

      System.out.println("Mapeado: " + httpMethod + " " + fullPath);
   }

   private static String formatPath(String prefix, String sub) {
      String path = (prefix + "/" + sub).replaceAll("/+", "/");
      if (!path.startsWith("/")) {
         path = "/" + path;
      }

      return path;
   }
}
