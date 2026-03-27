// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.routing;

public class Route {
   private String method;
   private String path;
   private RouteHandler handler;

   public Route(String method, String path, RouteHandler handler) {
      this.method = method;
      this.path = path;
      this.handler = handler;
   }

   public boolean matches(String method, String path) {
      return this.method.equalsIgnoreCase(method) && this.path.equals(path);
   }

   public RouteHandler getHandler() {
      return this.handler;
   }

   public String getPath() {
      return this.path;
   }
}
