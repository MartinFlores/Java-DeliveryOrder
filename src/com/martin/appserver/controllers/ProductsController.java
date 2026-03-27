package com.martin.appserver.controllers;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.*;
import com.martin.appserver.routing.Body;
import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.routing.Post;
import com.martin.appserver.services.InventoryService;
import com.martin.appserver.services.ProductService;
import com.martin.appserver.utils.JsonResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("/api/v1/products")
public class ProductsController {

   private final Gson gson = new Gson();

   @Get("/all")
   public String getAllProducts() {
      try {
         List<ProductDto> products = ProductService.getAllProducts();
         return JsonResponse.create().put("status", "ok").put("data", products).send();
      } catch (Exception e) {
         return JsonResponse.create().put("status", "error").put("message", e.getMessage()).send();
      }
   }

   @Post("/create")
   public String createProduct(@Body ProductDto body) {
      try {
         if (body.name == null || body.name.trim().isEmpty()) {
            return JsonResponse.create().put("status", "error").put("message", "Nombre obligatorio").send();
         }

         Map<String, Object> data = new HashMap<>();
         data.put("name", body.name);
         data.put("description", body.description);
         data.put("price", body.price);
         data.put("purchase_price", body.purchase_price != null ? body.purchase_price : 0.0);
         data.put("type", body.type != null ? body.type : "simple");
         data.put("status", body.status != null ? body.status : "active");
         data.put("created_at", System.currentTimeMillis());

         if (body.images != null) {
            data.put("images", gson.toJson(body.images));
         } else {
            data.put("images", "[]");
         }

         // ERP LOGIC: Handle track_inventory for Simple
         if ("simple".equals(body.type) && body.track_inventory != null && body.track_inventory) {
            if (body.inventory_item_id != null && body.inventory_item_id > 0) {
               data.put("inventory_item_id", body.inventory_item_id);
            } else if (body.inventory_item_data != null) {
               // Create new inventory item
               long itemId = InventoryService.createInventoryItem(
                     body.inventory_item_data.name,
                     body.inventory_item_data.unit,
                     body.inventory_item_data.min_stock_alert != null ? body.inventory_item_data.min_stock_alert : 0);

               // Create initial movement (initial purchase/adjustment)
               if (body.inventory_item_data.stock != null && body.inventory_item_data.stock > 0) {
                  InventoryService.addMovement(itemId, "purchase", body.inventory_item_data.stock, null);
               }

               data.put("inventory_item_id", itemId);
            }
         } else {
            data.put("inventory_item_id", null);
         }

         long productId = DBAdapter.insert("products", data);

         // HANDLE RECIPE FOR COMPOSED
         if ("composed".equals(body.type) && body.recipe != null) {
            for (RecipeItemDto item : body.recipe) {
               Map<String, Object> recipeData = new HashMap<>();
               recipeData.put("product_id", productId);
               recipeData.put("inventory_item_id", item.inventory_item_id);
               recipeData.put("quantity_required", item.quantity_required);
               DBAdapter.insert("product_recipes", recipeData);
            }
         }

         // Categories Junction
         if (body.category_ids != null) {
            for (Long catId : body.category_ids) {
               Map<String, Object> junction = new HashMap<>();
               junction.put("product_id", productId);
               junction.put("category_id", catId);
               DBAdapter.insert("product_categories", junction);
            }
         }

         return JsonResponse.create().put("status", "ok").put("id", productId).send();
      } catch (Exception e) {
         e.printStackTrace();
         return JsonResponse.create().put("status", "error").put("message", e.getMessage()).send();
      }
   }

   @Post("/update")
   public String updateProduct(@Body ProductDto body) {
      try {
         if (body.id == null)
            return JsonResponse.create().put("status", "error").put("message", "ID faltante").send();

         Map<String, Object> data = new HashMap<>();
         data.put("name", body.name);
         data.put("description", body.description);
         data.put("price", body.price);
         data.put("purchase_price", body.purchase_price);
         data.put("status", body.status);
         data.put("type", body.type);

         if (body.images != null) {
            data.put("images", gson.toJson(body.images));
         }

         // Handle Simple updates
         if ("simple".equals(body.type)) {
            if (body.track_inventory != null && body.track_inventory) {
               data.put("inventory_item_id", body.inventory_item_id);
            } else {
               data.put("inventory_item_id", null);
            }
         } else {
            data.put("inventory_item_id", null);
         }

         DBAdapter.update("products", data, "id = " + body.id);

         // Update Recipe if Composed
         if ("composed".equals(body.type)) {
            DBAdapter.delete("product_recipes", "product_id = " + body.id);
            if (body.recipe != null) {
               for (RecipeItemDto item : body.recipe) {
                  Map<String, Object> recipeData = new HashMap<>();
                  recipeData.put("product_id", body.id);
                  recipeData.put("inventory_item_id", item.inventory_item_id);
                  recipeData.put("quantity_required", item.quantity_required);
                  DBAdapter.insert("product_recipes", recipeData);
               }
            }
         } else {
            DBAdapter.delete("product_recipes", "product_id = " + body.id);
         }

         // Categories update
         if (body.category_ids != null) {
            DBAdapter.delete("product_categories", "product_id = " + body.id);
            for (Long catId : body.category_ids) {
               Map<String, Object> junction = new HashMap<>();
               junction.put("product_id", body.id);
               junction.put("category_id", catId);
               DBAdapter.insert("product_categories", junction);
            }
         }

         return JsonResponse.create().put("status", "ok").send();
      } catch (Exception e) {
         return JsonResponse.create().put("status", "error").put("message", e.getMessage()).send();
      }
   }

   @Post("/delete")
   public String deleteProduct(@Body ProductDto body) {
      try {
         DBAdapter.delete("products", "id = " + body.id);
         DBAdapter.delete("product_categories", "product_id = " + body.id);
         DBAdapter.delete("product_recipes", "product_id = " + body.id);
         return JsonResponse.create().put("status", "ok").send();
      } catch (Exception e) {
         return JsonResponse.create().put("status", "error").put("message", e.getMessage()).send();
      }
   }

   // Inventory Endpoints
   @Get("/inventory/items")
   public String getInventoryItems() {
      return JsonResponse.create().put("status", "ok").put("data", InventoryService.getAllItems()).send();
   }

   @Post("/inventory/create")
   public String createInventoryItem(@Body InventoryItemDto body) {
      long id = InventoryService.createInventoryItem(body.name, body.unit, body.min_stock_alert);

      // Handle Initial Stock if provided
      if (body.stock != null && body.stock > 0) {
         InventoryService.addMovement(id, "purchase", body.stock, null);
      }

      return JsonResponse.create().put("status", "ok").put("id", id).send();
   }

   @Post("/inventory/movement")
   public String addMovement(@Body InventoryMovementDto body) {
      InventoryService.addMovement(body.inventory_item_id, body.type, body.quantity, body.reference_id);
      return JsonResponse.create().put("status", "ok").send();
   }

   @Get("/categories")
   public String getCategories() {
      return JsonResponse.create().put("status", "ok")
            .put("data", DBAdapter.query("SELECT * FROM categories ORDER BY name ASC")).send();
   }

   @Post("/categories/create")
   public String createCategory(@Body CategoryDto body) {
      Map<String, Object> data = new HashMap<>();
      data.put("name", body.name);
      data.put("icon", body.icon != null ? body.icon : "Package");
      data.put("created_at", System.currentTimeMillis());
      long id = DBAdapter.insert("categories", data);
      return JsonResponse.create().put("status", "ok").put("id", id).send();
   }
}
