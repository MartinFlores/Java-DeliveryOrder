package com.martin.appserver.dto;

public class InventoryMovementDto {
    public Long id;
    public Long inventory_item_id;
    public String type; // "sale" | "purchase" | "adjustment"
    public Double quantity;
    public Long reference_id;
    public Long created_at;
}
