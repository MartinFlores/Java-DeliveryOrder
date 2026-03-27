// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.UserDto;
import com.martin.appserver.routing.Body;
import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.routing.HttpCode;
import com.martin.appserver.routing.Post;
import com.martin.appserver.utils.JsonResponse;
import com.martin.appserver.validation.ValidationException;
import com.martin.appserver.validation.Validator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/api/v1/users")
public class UsersController {
   public UsersController() {
   }

   @Get("/")
   public String getAllUsers(Map<String, String> queryParams) {
      try {
         List<Map<String, Object>> users = DBAdapter.query("SELECT * FROM users ORDER BY id DESC");
         return JsonResponse.create().put("status", "ok").put("data", users).send();
      } catch (Exception var3) {
         return JsonResponse.create().put("status", "error").put("message", var3.getMessage()).send();
      }
   }

   @Post("/create")
   @HttpCode(200)
   public String createUser(@Body UserDto body) {
      try {
         Validator.validate(body);
         if (body.whatsapp != null && !body.whatsapp.isEmpty()) {
            List<Map<String, Object>> existing = DBAdapter.query("SELECT id FROM users WHERE whatsapp = '" + body.whatsapp + "'");
            if (!existing.isEmpty()) {
               return JsonResponse.create().put("status", "error").put("message", "Este número de WhatsApp ya se encuentra registrado").send();
            }
         }

         Map<String, Object> userData = new HashMap();
         userData.put("name", body.name);
         userData.put("whatsapp", body.whatsapp);
         userData.put("password", body.password);
         userData.put("role", body.role);
         userData.put("status", body.status);
         userData.put("avatar", body.avatar);
         userData.put("created_at", System.currentTimeMillis());
         long userId = DBAdapter.insert("users", userData);
         return userId > 0L ? JsonResponse.create().put("status", "ok").put("id", userId).put("message", "Usuario registrado correctamente").send() : JsonResponse.create().put("status", "error").put("message", "Error al crear usuario").send();
      } catch (ValidationException var5) {
         return JsonResponse.create().put("status", "error").put("message", var5.getMessage()).send();
      } catch (Exception var6) {
         return JsonResponse.create().put("status", "error").put("message", var6.getMessage()).send();
      }
   }

   @Post("/update")
   public String updateUser(@Body UserDto body) {
      try {
         if (body.id == null) {
            return JsonResponse.create().put("status", "error").put("message", "ID de usuario es requerido").send();
         } else {
            if (body.whatsapp != null && !body.whatsapp.isEmpty()) {
               List<Map<String, Object>> existing = DBAdapter.query("SELECT id FROM users WHERE whatsapp = '" + body.whatsapp + "' AND id != " + body.id);
               if (!existing.isEmpty()) {
                  return JsonResponse.create().put("status", "error").put("message", "Este número de WhatsApp ya está en uso por otro usuario").send();
               }
            }

            Map<String, Object> userData = new HashMap();
            if (body.name != null) {
               userData.put("name", body.name);
            }

            if (body.whatsapp != null) {
               userData.put("whatsapp", body.whatsapp);
            }

            if (body.password != null && !body.password.isEmpty()) {
               userData.put("password", body.password);
            }

            if (body.role != null) {
               userData.put("role", body.role);
            }

            if (body.status != null) {
               userData.put("status", body.status);
            }

            if (body.avatar != null) {
               userData.put("avatar", body.avatar);
            }

            int rows = DBAdapter.update("users", userData, "id = " + body.id);
            return rows > 0 ? JsonResponse.create().put("status", "ok").put("message", "Usuario actualizado correctamente").send() : JsonResponse.create().put("status", "error").put("message", "No se encontró el usuario para actualizar").send();
         }
      } catch (Exception var4) {
         return JsonResponse.create().put("status", "error").put("message", var4.getMessage()).send();
      }
   }

   @Post("/delete")
   public String deleteUser(@Body UserDto body) {
      try {
         if (body.id == null) {
            return JsonResponse.create().put("status", "error").put("message", "ID de usuario es requerido").send();
         } else {
            int rows = DBAdapter.delete("users", "id = " + body.id);
            return rows > 0 ? JsonResponse.create().put("status", "ok").put("message", "Usuario eliminado correctamente").send() : JsonResponse.create().put("status", "error").put("message", "No se encontró el usuario para eliminar").send();
         }
      } catch (Exception var3) {
         return JsonResponse.create().put("status", "error").put("message", var3.getMessage()).send();
      }
   }

   @Post("/login")
   public String login(@Body Map<String, String> body) {
      try {
         String whatsapp = (String)body.get("whatsapp");
         String password = (String)body.get("password");
         if (whatsapp != null && !whatsapp.isEmpty() && password != null && !password.isEmpty()) {
            List<Map<String, Object>> users = DBAdapter.query("SELECT * FROM users WHERE whatsapp = '" + whatsapp + "' AND password = '" + password + "' AND status = 'Activo' LIMIT 1");
            if (!users.isEmpty()) {
               Map<String, Object> user = (Map)users.get(0);
               user.remove("password");
               return JsonResponse.create().put("status", "ok").put("data", user).put("message", "Login exitoso").send();
            } else {
               return JsonResponse.create().put("status", "error").put("message", "Credenciales incorrectas").send();
            }
         } else {
            return JsonResponse.create().put("status", "error").put("message", "WhatsApp y contraseña son requeridos").send();
         }
      } catch (Exception var6) {
         return JsonResponse.create().put("status", "error").put("message", var6.getMessage()).send();
      }
   }
}
