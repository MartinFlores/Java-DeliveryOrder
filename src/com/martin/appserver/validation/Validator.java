// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.validation;

import java.lang.reflect.Field;

public class Validator {
   public Validator() {
   }

   public static void validate(Object obj) throws ValidationException {
      if (obj == null) {
         throw new ValidationException("Body cannot be null");
      } else {
         Field[] fields = obj.getClass().getDeclaredFields();
         Field[] var2 = fields;
         int var3 = fields.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field field = var2[var4];
            field.setAccessible(true);

            try {
               Object value = field.get(obj);
               if (field.isAnnotationPresent(IsString.class) && value != null && !(value instanceof String)) {
                  throw new ValidationException(field.getName() + " must be a string");
               }

               if (field.isAnnotationPresent(NotEmpty.class) && (value == null || value instanceof String && ((String)value).isEmpty())) {
                  throw new ValidationException(field.getName() + " should not be empty");
               }
            } catch (IllegalAccessException var7) {
               throw new ValidationException("Validation error: " + var7.getMessage());
            }
         }

      }
   }
}
