// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.dto;

import com.martin.appserver.validation.IsString;
import com.martin.appserver.validation.NotEmpty;

public class CompanyDto {
   @IsString
   @NotEmpty
   public String businessName;
   @IsString
   @NotEmpty
   public String username;
   @IsString
   @NotEmpty
   public String pin;
   @IsString
   @NotEmpty
   public String brandColor;

   public CompanyDto() {
   }
}
