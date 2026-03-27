// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.controllers;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.ShiftDto;
import com.martin.appserver.routing.Body;
import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.routing.Post;
import com.martin.appserver.utils.JsonResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/api/v1/shifts")
public class ShiftsController {
   public ShiftsController() {
   }

   @Get("/active")
   public String getActiveShift(Map<String, String> queryParams) {
      try {
         String userId = (String) queryParams.get("user_id");
         if (userId == null) {
            return JsonResponse.create().put("status", "error").put("message", "User ID is required").send();
         } else {
            List<Map<String, Object>> shifts = DBAdapter
                  .query("SELECT * FROM shifts WHERE user_id = " + userId + " AND status = 'Abierto' LIMIT 1");
            return !shifts.isEmpty() ? JsonResponse.create().put("status", "ok").put("data", shifts.get(0)).send()
                  : JsonResponse.create().put("status", "none").put("message", "No active shift found").send();
         }
      } catch (Exception var4) {
         return JsonResponse.create().put("status", "error").put("message", var4.getMessage()).send();
      }
   }

   @Post("/open")
   public String openShift(@Body ShiftDto body) {
      try {
         if (body.user_id != null && body.opening_amount != null) {
            Map<String, Object> closePrev = new HashMap();
            closePrev.put("status", "Cerrado");
            closePrev.put("closed_at", System.currentTimeMillis());
            DBAdapter.update("shifts", closePrev, "user_id = " + body.user_id + " AND status = 'Abierto'");
            Map<String, Object> shiftData = new HashMap();
            shiftData.put("user_id", body.user_id);
            shiftData.put("opened_at", System.currentTimeMillis());
            shiftData.put("opening_amount", body.opening_amount);
            shiftData.put("status", "Abierto");
            long shiftId = DBAdapter.insert("shifts", shiftData);
            if (shiftId > 0L) {
               shiftData.put("id", shiftId);
               return JsonResponse.create().put("status", "ok").put("id", shiftId).put("data", shiftData)
                     .put("message", "Turno abierto correctamente").send();
            } else {
               return JsonResponse.create().put("status", "error").put("message", "Error al abrir turno").send();
            }
         } else {
            return JsonResponse.create().put("status", "error")
                  .put("message", "User ID and opening amount are required").send();
         }
      } catch (Exception var6) {
         return JsonResponse.create().put("status", "error").put("message", var6.getMessage()).send();
      }
   }

   @Post("/close")
   public String closeShift(@Body ShiftDto body) {
      try {
         if (body.id != null && body.closing_amount != null) {
            Map<String, Object> shiftData = new HashMap();
            shiftData.put("closing_amount", body.closing_amount);
            shiftData.put("closed_at", System.currentTimeMillis());
            shiftData.put("status", "Cerrado");
            int rows = DBAdapter.update("shifts", shiftData, "id = " + body.id);
            return rows > 0
                  ? JsonResponse.create().put("status", "ok").put("message", "Turno cerrado correctamente").send()
                  : JsonResponse.create().put("status", "error").put("message", "No se encontró el turno para cerrar")
                        .send();
         } else {
            return JsonResponse.create().put("status", "error")
                  .put("message", "Shift ID and closing amount are required").send();
         }
      } catch (Exception var4) {
         return JsonResponse.create().put("status", "error").put("message", var4.getMessage()).send();
      }
   }

   @Get("/stats")
   public String getShiftStats(Map<String, String> queryParams) {
      try {
         String shiftId = (String) queryParams.get("shift_id");
         if (shiftId == null) {
            return JsonResponse.create().put("status", "error").put("message", "Shift ID is required").send();
         } else {
            List<Map<String, Object>> totals = DBAdapter.query(
                  "SELECT COUNT(*) as total_orders, SUM(total) as total_sales, (SELECT SUM(amount) FROM order_payments op JOIN orders o ON op.order_id = o.id WHERE o.shift_id = "
                        + shiftId + " AND op.payment_method = 'Efectivo') as cash_sales FROM orders WHERE shift_id = "
                        + shiftId);
            List<Map<String, Object>> paymentMethodTotals = DBAdapter.query(
                  "SELECT op.payment_method, SUM(op.amount) as amount FROM order_payments op JOIN orders o ON op.order_id = o.id WHERE o.shift_id = "
                        + shiftId + " GROUP BY op.payment_method");
            Map<String, Object> data = new HashMap();
            if (!totals.isEmpty()) {
               data.put("general", totals.get(0));
            }

            data.put("payments", paymentMethodTotals);
            return JsonResponse.create().put("status", "ok").put("data", data).send();
         }
      } catch (Exception var6) {
         return JsonResponse.create().put("status", "error").put("message", var6.getMessage()).send();
      }
   }

   @Get("/list")
   public String getShiftsByDate(Map<String, String> queryParams) {
      try {
         String date = (String) queryParams.get("date");
         if (date == null) {
            return JsonResponse.create().put("status", "error").put("message", "Date is required").send();
         } else {
            String sql = "SELECT s.*, u.name as user_name, (SELECT SUM(total) FROM orders WHERE shift_id = s.id) as total_sales, (SELECT SUM(amount) FROM order_payments op JOIN orders o ON op.order_id = o.id WHERE o.shift_id = s.id AND op.payment_method = 'Efectivo') as cash_sales, (SELECT COUNT(*) FROM orders WHERE shift_id = s.id) as total_orders FROM shifts s JOIN users u ON s.user_id = u.id WHERE date(s.opened_at / 1000, 'unixepoch', 'localtime') = '"
                  + date + "' ORDER BY s.opened_at DESC";
            List<Map<String, Object>> shifts = DBAdapter.query(sql);
            return JsonResponse.create().put("status", "ok").put("data", shifts).send();
         }
      } catch (Exception var5) {
         return JsonResponse.create().put("status", "error").put("message", var5.getMessage()).send();
      }
   }
}
