// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.database;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBAdapter {
   private static boolean isAndroid = false;

   public DBAdapter() {
   }

   public static void initialize(Object context) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            dbClass.getMethod("initialize", Object.class).invoke((Object) null, context);
         } catch (Exception var2) {
            System.err.println("Error inicializando DB Android: " + var2.getMessage());
         }
      } else {
         StandaloneDB.initialize();
      }

   }

   public static void execSQL(String sql) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            Object db = dbClass.getMethod("getDatabase").invoke((Object) null);
            db.getClass().getMethod("execSQL", String.class).invoke(db, sql);
         } catch (Exception var3) {
            System.err.println("Error execSQL Android: " + var3.getMessage());
         }
      } else {
         StandaloneDB.execSQL(sql);
      }

   }

   public static long insert(String table, Map<String, Object> values) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            return (Long) dbClass.getMethod("insert", String.class, Map.class).invoke((Object) null, table, values);
         } catch (Exception var3) {
            System.err.println("Error insert Android: " + var3.getMessage());
            return -1L;
         }
      } else {
         return StandaloneDB.insert(table, values);
      }
   }

   public static String getLastError() {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            return (String) dbClass.getMethod("getLastError").invoke(null);
         } catch (Exception e) {
            return e.getMessage();
         }
      }
      return null;
   }

   public static List<Map<String, Object>> query(String sql) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            Object jsonArray = dbClass.getMethod("rawQuery", String.class).invoke((Object) null, sql);
            return jsonArrayToList(jsonArray);
         } catch (Exception var3) {
            System.err.println("Error query Android: " + var3.getMessage());
            return new ArrayList();
         }
      } else {
         ResultSet rs = StandaloneDB.query(sql);
         return resultSetToList(rs);
      }
   }

   private static List<Map<String, Object>> resultSetToList(ResultSet rs) {
      List<Map<String, Object>> list = new ArrayList();
      if (rs == null) {
         return list;
      } else {
         try {
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            while (rs.next()) {
               Map<String, Object> row = new HashMap(columns);

               for (int i = 1; i <= columns; ++i) {
                  row.put(md.getColumnName(i), rs.getObject(i));
               }

               list.add(row);
            }

            rs.getStatement().close();
            rs.close();
         } catch (Exception var6) {
            System.err.println("Error converting ResultSet: " + var6.getMessage());
         }

         return list;
      }
   }

   private static List<Map<String, Object>> jsonArrayToList(Object array) {
      List<Map<String, Object>> list = new ArrayList();
      if (array == null) {
         return list;
      } else {
         try {
            Method lengthMethod = array.getClass().getMethod("length");
            Method getJSONObjectMethod = array.getClass().getMethod("getJSONObject", Integer.TYPE);
            int length = (Integer) lengthMethod.invoke(array);

            for (int i = 0; i < length; ++i) {
               Object obj = getJSONObjectMethod.invoke(array, i);
               Map<String, Object> row = new HashMap();
               Method namesMethod = obj.getClass().getMethod("names");
               Object keys = namesMethod.invoke(obj);
               if (keys != null) {
                  Method keyLengthMethod = keys.getClass().getMethod("length");
                  Method getStringMethod = keys.getClass().getMethod("getString", Integer.TYPE);
                  Method getMethod = obj.getClass().getMethod("get", String.class);
                  int keyLength = (Integer) keyLengthMethod.invoke(keys);

                  for (int j = 0; j < keyLength; ++j) {
                     String key = (String) getStringMethod.invoke(keys, j);
                     row.put(key, getMethod.invoke(obj, key));
                  }
               }

               list.add(row);
            }
         } catch (Exception var16) {
            System.err.println("Error converting JSONArray via reflection: " + var16.getMessage());
         }

         return list;
      }
   }

   public static Object queryAndroid(String sql) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            return dbClass.getMethod("rawQuery", String.class).invoke((Object) null, sql);
         } catch (Exception var2) {
            System.err.println("Error query Android: " + var2.getMessage());
         }
      }

      return null;
   }

   public static int update(String table, Map<String, Object> values, String where) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            String[] parts = where.split("=");
            String column = parts[0].trim();
            String value = parts[1].trim();
            Object dbInstance = dbClass.getMethod("where", String.class, Object.class).invoke((Object) null, column,
                  value);
            return (Integer) dbInstance.getClass().getMethod("update", String.class, Map.class).invoke(dbInstance,
                  table, values);
         } catch (Exception var8) {
            System.err.println("Error update Android: " + var8.getMessage());
            var8.printStackTrace();
            return 0;
         }
      } else {
         return StandaloneDB.update(table, values, where);
      }
   }

   public static int delete(String table, String where) {
      if (isAndroid) {
         try {
            Class<?> dbClass = Class.forName("com.martin.appserver.database.DB");
            String[] parts = where.split("=");
            String column = parts[0].trim();
            String value = parts[1].trim();
            Object dbInstance = dbClass.getMethod("where", String.class, Object.class).invoke((Object) null, column,
                  value);
            return (Integer) dbInstance.getClass().getMethod("delete", String.class).invoke(dbInstance, table);
         } catch (Exception var7) {
            System.err.println("Error delete Android: " + var7.getMessage());
            return 0;
         }
      } else {
         return StandaloneDB.delete(table, where);
      }
   }

   static {
      try {
         Class.forName("android.database.sqlite.SQLiteDatabase");
         isAndroid = true;
      } catch (ClassNotFoundException var1) {
         isAndroid = false;
      }

   }
}
