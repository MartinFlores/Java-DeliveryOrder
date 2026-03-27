// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.utils.ServerLogger;
import java.util.Map;

@Controller("/api/logs")
public class LogsController {
   public LogsController() {
   }

   @Get("/get")
   public String getLogs(Map<String, String> queryParams) {
      return ServerLogger.getLogsJson();
   }

   @Get("/clear")
   public String clearLogs(Map<String, String> queryParams) {
      ServerLogger.clear();
      return "{\"status\":\"ok\",\"message\":\"Logs limpiados\"}";
   }
}
