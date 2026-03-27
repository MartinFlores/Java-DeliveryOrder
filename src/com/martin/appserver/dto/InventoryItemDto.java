package com.martin.appserver.dto;

public class InventoryItemDto {
    public Long id;
    public String name;
    public String unit;
    public Integer min_stock_alert;
    public Double stock; // Calculated (SUM movements)
    public Long created_at;
}
