// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.utils.JsonResponse;
import java.util.List;
import java.util.Map;

@Controller("/api/v1/config")
public class ConfigStatusController {
   public ConfigStatusController() {
   }

   @Get("/status")
   public String getStatus() {
      try {
         List<Map<String, Object>> results = DBAdapter.query("SELECT company_name FROM company_config WHERE id = 1");
         if (results != null && !results.isEmpty()) {
            Map<String, Object> firstRow = (Map)results.get(0);
            Object companyNameObj = firstRow.get("company_name");
            String companyName = companyNameObj != null ? companyNameObj.toString() : "";
            boolean isConfigured = !companyName.isEmpty();
            return JsonResponse.create().put("configured", isConfigured).send();
         } else {
            return JsonResponse.create().put("configured", false).send();
         }
      } catch (Exception var6) {
         return JsonResponse.create().put("configured", false).send();
      }
   }
}
