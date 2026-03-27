// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.utils.StandaloneNetworkUtils;
import java.util.Map;

@Controller("/network")
public class NetworkController {
   public NetworkController() {
   }

   @Get("/get-ip")
   public String handle(Map<String, String> queryParams) {
      String ip = StandaloneNetworkUtils.getLocalIp();
      return "{\"ip\":\"" + ip + "\"}";
   }
}
