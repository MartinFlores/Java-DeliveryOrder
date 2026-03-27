// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.dto;

import java.util.List;

public class OrderDto {
   public Long id;
   public Long user_id;
   public Long shift_id;
   public String customer_name;
   public Double total;
   public String payment_method;
   public String status;
   public Long created_at;
   public List<OrderItemDto> items;
   public List<PaymentDto> payments;

   public OrderDto() {
   }

   public static class PaymentDto {
      public String payment_method;
      public Double amount;

      public PaymentDto() {
      }
   }

   public static class OrderItemDto {
      public Long product_id;
      public Integer quantity;
      public Double price;
      public Double subtotal;

      public OrderItemDto() {
      }
   }
}
