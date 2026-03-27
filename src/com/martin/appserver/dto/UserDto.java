// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.dto;

import com.martin.appserver.validation.IsString;
import com.martin.appserver.validation.NotEmpty;

public class UserDto {
   @IsString
   @NotEmpty
   public String name;
   @IsString
   @NotEmpty
   public String whatsapp;
   @IsString
   @NotEmpty
   public String password;
   @IsString
   @NotEmpty
   public String role;
   @IsString
   @NotEmpty
   public String status;
   public String avatar;
   public Long id;

   public UserDto() {
   }
}
