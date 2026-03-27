// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.utils.StandaloneNetworkUtils;

@Controller("/api/v1")
public class ConfigController {
   public ConfigController() {
   }

   @Get("/config")
   public String getConfig() {
      String ip = StandaloneNetworkUtils.getLocalIp();
      String baseUrl = "http://" + ip + ":" + 7979;
      return "{\"api\":{\"baseUrl\":\"" + baseUrl + "\",\"version\":\"v1\"},\"apps\":{\"superAdmin\":\"" + baseUrl + "/superAdmin\",\"cocina\":\"" + baseUrl + "/cocina\",\"cajero\":\"" + baseUrl + "/cajero\",\"mesero\":\"" + baseUrl + "/mesero\",\"menu\":\"" + baseUrl + "/menu\"}}";
   }
}
