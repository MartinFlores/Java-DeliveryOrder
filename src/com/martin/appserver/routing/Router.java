// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.routing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Router {
   private static List<Route> routes = new ArrayList();

   public Router() {
   }

   public static void get(String path, RouteHandler handler) {
      routes.add(new Route("GET", path, handler));
   }

   public static void post(String path, RouteHandler handler) {
      routes.add(new Route("POST", path, handler));
   }

   public static void put(String path, RouteHandler handler) {
      routes.add(new Route("PUT", path, handler));
   }

   public static void delete(String path, RouteHandler handler) {
      routes.add(new Route("DELETE", path, handler));
   }

   public static Route find(String method, String path) {
      Iterator var2 = routes.iterator();

      Route route;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         route = (Route)var2.next();
      } while(!route.matches(method, path));

      return route;
   }

   public static String execute(Route route, Map<String, String> queryParams, String body) {
      try {
         if (route == null) {
            return null;
         } else {
            Object result = route.getHandler().handle(queryParams, body);
            return result != null ? result.toString() : "";
         }
      } catch (Exception var4) {
         System.err.println("Error: " + var4.getMessage());
         return "{\"error\":\"Internal Server Error\"}";
      }
   }

   public static int count() {
      return routes.size();
   }

   public static void clear() {
      routes.clear();
   }
}
