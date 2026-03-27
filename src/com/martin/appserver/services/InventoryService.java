package com.martin.appserver.services;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.dto.InventoryItemDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {

    public static Double getStock(Long inventoryItemId) {
        if (inventoryItemId == null)
            return 0.0;

        String sql = "SELECT SUM(quantity) as stock FROM inventory_movements WHERE inventory_item_id = "
                + inventoryItemId;
        List<Map<String, Object>> results = DBAdapter.query(sql);
        if (!results.isEmpty()) {
            Map<String, Object> obj = results.get(0);
            Object stockValue = obj.get("stock");
            if (stockValue == null)
                return 0.0;
            return ((Number) stockValue).doubleValue();
        }
        return 0.0;
    }

    public static Long createInventoryItem(String name, String unit, Integer minStockAlert) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("unit", unit);
        data.put("min_stock_alert", minStockAlert);
        data.put("created_at", System.currentTimeMillis());

        return DBAdapter.insert("inventory_items", data);
    }

    public static void addMovement(Long inventoryItemId, String type, Double quantity, Long referenceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("inventory_item_id", inventoryItemId);
        data.put("type", type);
        data.put("quantity", quantity);
        data.put("reference_id", referenceId);
        data.put("created_at", System.currentTimeMillis());

        DBAdapter.insert("inventory_movements", data);
    }

    public static List<InventoryItemDto> getAllItems() {
        List<Map<String, Object>> items = DBAdapter.query("SELECT * FROM inventory_items");
        List<InventoryItemDto> list = new ArrayList<>();

        for (Map<String, Object> obj : items) {
            InventoryItemDto dto = new InventoryItemDto();
            dto.id = ((Number) obj.get("id")).longValue();
            dto.name = (String) obj.get("name");
            dto.unit = (String) obj.get("unit");
            dto.min_stock_alert = ((Number) obj.get("min_stock_alert")).intValue();
            dto.created_at = ((Number) obj.get("created_at")).longValue();
            dto.stock = getStock(dto.id);
            list.add(dto);
        }
        return list;
    }

    public static InventoryItemDto getItem(Long id) {
        List<Map<String, Object>> results = DBAdapter
                .query("SELECT * FROM inventory_items WHERE id = " + id + " LIMIT 1");
        if (results.isEmpty())
            return null;

        Map<String, Object> obj = results.get(0);
        InventoryItemDto dto = new InventoryItemDto();
        dto.id = ((Number) obj.get("id")).longValue();
        dto.name = (String) obj.get("name");
        dto.unit = (String) obj.get("unit");
        dto.min_stock_alert = ((Number) obj.get("min_stock_alert")).intValue();
        dto.created_at = ((Number) obj.get("created_at")).longValue();
        dto.stock = getStock(dto.id);
        return dto;
    }
}
