package com.martin.appserver.dto;

import java.util.List;

public class ProductDto {
    public Long id;
    public String name;
    public String description;
    public Double price;
    public Double purchase_price;
    public String type; // "simple" | "composed"
    public Long inventory_item_id; // nullable
    public String status; // "active" | "hidden"
    public List<String> images;
    public List<Long> category_ids;
    public Double stock; // Calculated dynamically
    public List<RecipeItemDto> recipe; // Only for composed
    public Long created_at;

    // For creation/update of simple products with inventory
    public Boolean track_inventory;
    public InventoryItemDto inventory_item_data;
}
