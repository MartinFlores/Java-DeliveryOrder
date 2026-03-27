// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.CompanyDto;
import com.martin.appserver.routing.Body;
import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.routing.Post;
import com.martin.appserver.utils.JsonResponse;
import com.martin.appserver.utils.ServerLogger;
import com.martin.appserver.validation.ValidationException;
import com.martin.appserver.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/api/v1/config")
public class CompanyConfigController {
   public CompanyConfigController() {
   }

   @Get("/setup")
   public String getSett() {
      return "{\"status\":\"ok\",\"message\":\"Logs limpiados\"}";
   }

   @Post("/setup")
   public String setupCompany(@Body CompanyDto body) {
      ServerLogger.log("POST /setup recibido");

      try {
         if (body == null) {
            ServerLogger.log("Body es null");
            return JsonResponse.create().put("error", "Body null").send();
         } else {
            ServerLogger.log("Body: " + body.businessName);
            Validator.validate(body);
            Map<String, Object> data = new HashMap();
            data.put("company_name", body.businessName);
            data.put("username", body.username);
            data.put("pin", body.pin);
            data.put("primary_color", body.brandColor);
            data.put("updated_at", System.currentTimeMillis());

            DBAdapter.execSQL("INSERT OR IGNORE INTO company_config (id) VALUES (1)");
            int updated = DBAdapter.update("company_config", data, "id = 1");
            ServerLogger.log("Updated rows: " + updated);
            return updated > 0 ? JsonResponse
                  .create()
                  .put("status", "ok")
                  .put("message", "Configuración guardada")
                  .put("company", data)
                  .send()
                  : JsonResponse.create().put("error", "No se pudo guardar").send();
         }
      } catch (ValidationException var4) {
         ServerLogger.log("ValidationException: " + var4.getMessage());
         return JsonResponse.create().put("error", var4.getMessage()).send();
      } catch (Exception var5) {
         ServerLogger.log("Exception: " + var5.getMessage());
         return JsonResponse.create().put("error", var5.getMessage()).send();
      }
   }

   @Get("/business-details")
   public String getBusinessDetails() {
      try {
         List<Map<String, Object>> results = DBAdapter
               .query("SELECT company_name, username, primary_color FROM company_config WHERE id = 1");
         if (results != null && !results.isEmpty()) {
            Map<String, Object> config = results.get(0);
            return JsonResponse.create()
                  .put("status", "ok")
                  .put("businessName", config.get("company_name"))
                  .put("username", config.get("username"))
                  .put("brandColor", config.get("primary_color"))
                  .send();
         } else {
            return JsonResponse.create().put("error", "Configuración no encontrada").send();
         }
      } catch (Exception e) {
         return JsonResponse.create().put("error", e.getMessage()).send();
      }
   }

   @Post("/admin-login")
   public String adminLogin(@Body Map<String, String> body) {
      try {
         String pin = body.get("pin");
         if (pin == null || pin.isEmpty()) {
            return JsonResponse.create().put("status", "error").put("message", "PIN es requerido").send();
         }

         List<Map<String, Object>> results = DBAdapter.query("SELECT pin, username FROM company_config WHERE id = 1");
         if (results != null && !results.isEmpty()) {
            Map<String, Object> config = results.get(0);
            String savedPin = (String) config.get("pin");
            if (pin.equals(savedPin)) {
               return JsonResponse.create()
                     .put("status", "ok")
                     .put("message", "Login exitoso")
                     .put("username", config.get("username"))
                     .send();
            } else {
               return JsonResponse.create().put("status", "error").put("message", "PIN incorrecto").send();
            }
         } else {
            return JsonResponse.create().put("status", "error").put("message", "Configuración no encontrada").send();
         }
      } catch (Exception e) {
         return JsonResponse.create().put("status", "error").put("message", e.getMessage()).send();
      }
   }
}
