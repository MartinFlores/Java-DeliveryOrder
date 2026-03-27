package com.martin.appserver.dto;

public class RecipeItemDto {
    public Long id;
    public Long product_id;
    public Long inventory_item_id;
    public String inventory_item_name;
    public Double quantity_required;
    public String unit;
    public Double available_stock; // Calculated dynamically
}
