package com.martin.appserver.controllers;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.OrderDto;
import com.martin.appserver.dto.ProductDto;
import com.martin.appserver.dto.RecipeItemDto;
import com.martin.appserver.routing.Body;
import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.routing.Post;
import com.martin.appserver.services.InventoryService;
import com.martin.appserver.services.ProductService;
import com.martin.appserver.utils.JsonResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/api/v1/orders")
public class OrdersController {

   @Post("/create")
   public String createOrder(@Body OrderDto body) {
      try {
         if (body.items == null || body.items.isEmpty()) {
            return JsonResponse.create().put("status", "error").put("message", "No hay items en la orden").send();
         }

         // 1. VALIDATE STOCK BEFORE PROCEEDING
         for (OrderDto.OrderItemDto item : body.items) {
            ProductDto product = ProductService.getProduct(item.product_id);
            if (product == null)
               continue;

            Double available = ProductService.calculateStock(item.product_id);
            if (available < item.quantity) {
               return JsonResponse.create()
                     .put("status", "error")
                     .put("message", "Stock insuficiente para: " + product.name + " (Disponible: " + available + ")")
                     .send();
            }
         }

         // 2. CREATE ORDER HEADER
         double totalPaid = 0.0;
         if (body.payments != null && !body.payments.isEmpty()) {
            for (OrderDto.PaymentDto p : body.payments) {
               if (p.amount != null)
                  totalPaid += p.amount;
            }
         } else if (body.payment_method != null && body.total != null) {
            totalPaid = body.total;
         }

         double orderTotal = body.total != null ? body.total : 0.0;
         String status = totalPaid >= orderTotal - 0.01 ? "completed" : "open";

         Map<String, Object> orderData = new HashMap<>();
         orderData.put("user_id", body.user_id);
         orderData.put("shift_id", body.shift_id);
         orderData.put("customer_name",
               (body.customer_name != null && !body.customer_name.trim().isEmpty()) ? body.customer_name
                     : "Público General");
         orderData.put("total", orderTotal);
         orderData.put("payment_method", body.payment_method != null ? body.payment_method : "Efectivo");
         orderData.put("status", status);
         orderData.put("created_at", System.currentTimeMillis());

         long orderId = DBAdapter.insert("orders", orderData);

         // 3. CREATE ITEMS AND MOVEMENTS
         for (OrderDto.OrderItemDto item : body.items) {
            // Save Order Item
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("order_id", orderId);
            itemData.put("product_id", item.product_id);
            itemData.put("quantity", item.quantity);
            itemData.put("price", item.price);
            itemData.put("subtotal", item.subtotal);
            DBAdapter.insert("order_items", itemData);

            // PROCESS INVENTORY MOVEMENTS
            ProductDto product = ProductService.getProduct(item.product_id);
            if (product != null) {
               if ("simple".equals(product.type) && product.inventory_item_id != null) {
                  // Simple Product tracking inventory
                  InventoryService.addMovement(product.inventory_item_id, "sale", (double) -item.quantity, orderId);
               } else if ("composed".equals(product.type) && product.recipe != null) {
                  // Composed Product: decrease all recipe items
                  for (RecipeItemDto recipeItem : product.recipe) {
                     double quantityToDecrease = recipeItem.quantity_required * item.quantity;
                     InventoryService.addMovement(recipeItem.inventory_item_id, "sale", -quantityToDecrease, orderId);
                  }
               }
            }
         }

         // 4. SAVE PAYMENTS
         if (body.payments != null && !body.payments.isEmpty()) {
            for (OrderDto.PaymentDto p : body.payments) {
               Map<String, Object> pData = new HashMap<>();
               pData.put("order_id", orderId);
               pData.put("payment_method", p.payment_method);
               pData.put("amount", p.amount);
               DBAdapter.insert("order_payments", pData);
            }
         } else if ("completed".equals(status)) {
            Map<String, Object> pData = new HashMap<>();
            pData.put("order_id", orderId);
            pData.put("payment_method", body.payment_method != null ? body.payment_method : "Efectivo");
            pData.put("amount", orderTotal);
            DBAdapter.insert("order_payments", pData);
         }

         return JsonResponse.create()
               .put("status", "ok")
               .put("order_id", orderId)
               .put("message", "Venta procesada con éxito")
               .send();

      } catch (Exception e) {
         return JsonResponse.create().put("status", "error").put("message", e.getMessage()).send();
      }
   }

   @Get("/shift")
   public String getOrdersByShift(Map<String, String> queryParams) {
      String shiftId = queryParams.get("shift_id");
      List<Map<String, Object>> orders = DBAdapter
            .query("SELECT * FROM orders WHERE shift_id = " + shiftId + " ORDER BY created_at DESC");
      return JsonResponse.create().put("status", "ok").put("data", orders).send();
   }

   @Get("/details")
   public String getOrderDetails(Map<String, String> queryParams) {
      String orderId = queryParams.get("order_id");
      List<Map<String, Object>> items = DBAdapter.query(
            "SELECT oi.*, p.name as product_name FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = "
                  + orderId);
      List<Map<String, Object>> payments = DBAdapter.query("SELECT * FROM order_payments WHERE order_id = " + orderId);
      return JsonResponse.create().put("status", "ok").put("items", items).put("payments", payments).send();
   }
}
