package com.martin.appserver.services;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.ProductDto;
import com.martin.appserver.dto.RecipeItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductService {

    public static Double calculateStock(Long productId) {
        List<Map<String, Object>> results = DBAdapter
                .query("SELECT * FROM products WHERE id = " + productId + " LIMIT 1");
        if (results.isEmpty())
            return 0.0;

        Map<String, Object> product = results.get(0);
        String type = (String) product.get("type");
        if ("simple".equals(type)) {
            Object itemIdObj = product.get("inventory_item_id");
            if (itemIdObj == null)
                return 9999.0; // Infinite stock if not tracked

            Long inventoryItemId = ((Number) itemIdObj).longValue();
            if (inventoryItemId == 0)
                return 9999.0;

            return InventoryService.getStock(inventoryItemId);
        } else if ("composed".equals(type)) {
            // Get recipe
            List<Map<String, Object>> recipe = DBAdapter
                    .query("SELECT * FROM product_recipes WHERE product_id = " + productId);
            if (recipe.isEmpty())
                return 0.0;

            Double minAvailability = Double.MAX_VALUE;
            for (Map<String, Object> step : recipe) {
                Long item_id = ((Number) step.get("inventory_item_id")).longValue();
                Double required = ((Number) step.get("quantity_required")).doubleValue();
                Double stock = InventoryService.getStock(item_id);

                Double availability = Math.floor(stock / required);
                if (availability < minAvailability)
                    minAvailability = availability;
            }
            return minAvailability == Double.MAX_VALUE ? 0.0 : minAvailability;
        }
        return 0.0;
    }

    public static List<RecipeItemDto> getRecipe(Long productId) {
        List<Map<String, Object>> recipe = DBAdapter
                .query("SELECT * FROM product_recipes WHERE product_id = " + productId);
        List<RecipeItemDto> list = new ArrayList<>();

        for (Map<String, Object> obj : recipe) {
            RecipeItemDto dto = new RecipeItemDto();
            dto.id = ((Number) obj.get("id")).longValue();
            dto.product_id = ((Number) obj.get("product_id")).longValue();
            dto.inventory_item_id = ((Number) obj.get("inventory_item_id")).longValue();
            dto.quantity_required = ((Number) obj.get("quantity_required")).doubleValue();

            // Enrich with name and unit
            List<Map<String, Object>> itemRes = DBAdapter
                    .query("SELECT * FROM inventory_items WHERE id = " + dto.inventory_item_id + " LIMIT 1");
            if (!itemRes.isEmpty()) {
                Map<String, Object> item = itemRes.get(0);
                dto.inventory_item_name = (String) item.get("name");
                dto.unit = (String) item.get("unit");
                dto.available_stock = InventoryService.getStock(dto.inventory_item_id);
            }
            list.add(dto);
        }
        return list;
    }

    public static List<ProductDto> getAllProducts() {
        List<Map<String, Object>> products = DBAdapter.query("SELECT * FROM products ORDER BY id DESC");
        List<ProductDto> list = new ArrayList<>();

        for (Map<String, Object> obj : products) {
            list.add(mapToDto(obj));
        }
        return list;
    }

    public static ProductDto getProduct(Long id) {
        List<Map<String, Object>> results = DBAdapter.query("SELECT * FROM products WHERE id = " + id + " LIMIT 1");
        if (results.isEmpty())
            return null;
        return mapToDto(results.get(0));
    }

    private static ProductDto mapToDto(Map<String, Object> obj) {
        ProductDto dto = new ProductDto();
        dto.id = ((Number) obj.get("id")).longValue();
        dto.name = (String) obj.get("name");
        dto.description = (String) obj.get("description");
        dto.price = ((Number) obj.get("price")).doubleValue();

        Object purchasePrice = obj.get("purchase_price");
        dto.purchase_price = purchasePrice != null ? ((Number) purchasePrice).doubleValue() : 0.0;

        dto.type = (String) obj.get("type");

        Object invId = obj.get("inventory_item_id");
        dto.inventory_item_id = invId != null ? ((Number) invId).longValue() : null;

        dto.status = (String) obj.get("status");
        dto.created_at = ((Number) obj.get("created_at")).longValue();

        // Calculated fields
        dto.stock = calculateStock(dto.id);
        if ("composed".equals(dto.type)) {
            dto.recipe = getRecipe(dto.id);
        }

        // Load categories
        List<Map<String, Object>> cats = DBAdapter
                .query("SELECT category_id FROM product_categories WHERE product_id = " + dto.id);
        dto.category_ids = new ArrayList<>();
        for (Map<String, Object> cat : cats) {
            dto.category_ids.add(((Number) cat.get("category_id")).longValue());
        }

        return dto;
    }
}
