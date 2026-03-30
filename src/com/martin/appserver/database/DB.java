// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DB {
   private static final String TAG = "DB";
   private static SQLiteDatabase database;
   private static Context appContext;
   private static String lastError = null;

   public static String getLastError() {
      return lastError;
   }

   private String table;
   private String whereClause;
   private final List<String> whereArgs = new ArrayList();
   private String orderBy;
   private Integer limit;
   private Integer offset;

   public DB() {
   }

   public static void initialize(Context context) {
      appContext = context.getApplicationContext();
      openDatabase();
      createTables();
   }

   public static Context getContext() {
      return appContext;
   }

   private static void openDatabase() {
      try {
         if (appContext == null) {
            return;
         }

         if (database == null || !database.isOpen()) {
            database = appContext.openOrCreateDatabase("appserver.db", 0, (SQLiteDatabase.CursorFactory) null);
            Log.d("DB", "Database opened: " + (database != null ? database.getPath() : "null"));
         }
      } catch (Exception var1) {
         Log.e("DB", "Error opening database", var1);
      }

   }

   private static void createTables() {
      try {
         String[] var0 = DBSchema.getCreateTableStatements();
         int var1 = var0.length;

         for (int var2 = 0; var2 < var1; ++var2) {
            String sql = var0[var2];
            database.execSQL(sql);
         }

         Log.d("DB", "Tablas creadas");
      } catch (Exception var4) {
         Log.e("DB", "Error creando tablas", var4);
      }

   }


   public static SQLiteDatabase getDatabase() {
      if (database == null || !database.isOpen()) {
         openDatabase();
      }

      return database;
   }

   private void reset() {
      this.table = null;
      this.whereClause = null;
      this.whereArgs.clear();
      this.orderBy = null;
      this.limit = null;
      this.offset = null;
   }

   public static DB where(String column, Object value) {
      return where(column, value, "=");
   }

   public static DB where(String column, Object value, String operator) {
      DB q = new DB();
      return q.whereInternal(column, value, operator);
   }

   private DB whereInternal(String column, Object value, String operator) {
      this.whereClause = column + " " + operator + " ?";
      this.whereArgs.add(String.valueOf(value));
      return this;
   }

   public DB orderBy(String column, String direction) {
      this.orderBy = column + " " + direction;
      return this;
   }

   public DB limit(int limit) {
      this.limit = limit;
      return this;
   }

   public DB offset(int offset) {
      this.offset = offset;
      return this;
   }

   public static JSONArray get(String table) {
      DB instance = new DB();
      return instance.from(table).get();
   }

   public DB from(String table) {
      this.table = table;
      return this;
   }

   public static JSONArray getAll(String table) {
      DB q = new DB();
      q.table = table;
      return q.getAsJson();
   }

   public static long insert(String table, Map<String, Object> data) {
      DB q = new DB();
      q.table = table;
      return q.insertInternal(data);
   }

   private long insertInternal(Map<String, Object> data) {
      ContentValues values = new ContentValues();
      Iterator var3 = data.keySet().iterator();

      while (var3.hasNext()) {
         String key = (String) var3.next();
         Object value = data.get(key);
         if (value == null) {
            values.putNull(key);
         } else if (value instanceof Integer) {
            values.put(key, (Integer) value);
         } else if (value instanceof Long) {
            values.put(key, (Long) value);
         } else if (value instanceof Double) {
            values.put(key, (Double) value);
         } else if (value instanceof Float) {
            values.put(key, (Float) value);
         } else if (value instanceof Boolean) {
            values.put(key, (Boolean) value);
         } else {
            values.put(key, String.valueOf(value));
         }
      }

      long id = -1;
      try {
         id = getDatabase().insertOrThrow(this.table, (String) null, values);
         lastError = null;
      } catch (Exception e) {
         lastError = e.getMessage();
         Log.e("DB", "Error inserting into " + this.table + ": " + e.getMessage());
      }
      this.reset();
      return id;
   }

   public int update(String table, Map<String, Object> data) {
      this.table = table;
      ContentValues values = new ContentValues();
      Iterator var4 = data.keySet().iterator();

      while (var4.hasNext()) {
         String key = (String) var4.next();
         values.put(key, String.valueOf(data.get(key)));
      }

      int rows = getDatabase().update(table, values, this.whereClause,
            (String[]) this.whereArgs.toArray(new String[0]));
      this.reset();
      return rows;
   }

   public int delete(String table) {
      this.table = table;
      int rows = getDatabase().delete(table, this.whereClause, (String[]) this.whereArgs.toArray(new String[0]));
      this.reset();
      return rows;
   }

   public void execSQL(String sql) {
      try {
         getDatabase().execSQL(sql);
         lastError = null;
      } catch (Exception e) {
         lastError = e.getMessage();
         Log.e("DB", "Error execSQL: " + e.getMessage());
      }
   }

   public JSONArray getAsJson() {
      JSONArray array = new JSONArray();

      try {
         StringBuilder sql = new StringBuilder();
         sql.append("SELECT * FROM ").append(this.table);
         if (this.whereClause != null && !this.whereClause.isEmpty()) {
            sql.append(" WHERE ").append(this.whereClause);
         }

         if (this.orderBy != null) {
            sql.append(" ORDER BY ").append(this.orderBy);
         }

         if (this.limit != null) {
            sql.append(" LIMIT ").append(this.limit);
            if (this.offset != null) {
               sql.append(" OFFSET ").append(this.offset);
            }
         }

         String[] args = this.whereArgs.isEmpty() ? null : (String[]) this.whereArgs.toArray(new String[0]);
         Cursor cursor = getDatabase().rawQuery(sql.toString(), args);
         if (cursor != null) {
            try {
               if (cursor.moveToFirst()) {
                  do {
                     JSONObject obj = new JSONObject();

                     for (int i = 0; i < cursor.getColumnCount(); ++i) {
                        String col = cursor.getColumnName(i);
                        String val = cursor.isNull(i) ? null : cursor.getString(i);
                        obj.put(col, val);
                     }

                     array.put(obj);
                  } while (cursor.moveToNext());
               }
            } finally {
               cursor.close();
            }
         }
      } catch (JSONException var14) {
         Log.e("DB", "JSON error converting results", var14);
      } catch (Exception var15) {
         Log.e("DB", "Error fetching results", var15);
      }

      this.reset();
      return array;
   }

   public static JSONArray rawQuery(String sql) {
      SQLiteDatabase db = getDatabase();
      JSONArray array = new JSONArray();
      Cursor cursor = db.rawQuery(sql, (String[]) null);
      if (cursor.moveToFirst()) {
         do {
            JSONObject obj = new JSONObject();

            for (int i = 0; i < cursor.getColumnCount(); ++i) {
               String column = cursor.getColumnName(i);
               switch (cursor.getType(i)) {
                  case 0:
                     obj.put(column, JSONObject.NULL);
                     break;
                  case 1:
                     obj.put(column, cursor.getLong(i));
                     break;
                  case 2:
                     obj.put(column, cursor.getDouble(i));
                     break;
                  case 3:
                     obj.put(column, cursor.getString(i));
                     break;
                  default:
                     obj.put(column, cursor.getString(i));
               }
            }

            array.put(obj);
         } while (cursor.moveToNext());
      }

      cursor.close();
      return array;
   }

   public JSONArray get() {
      return this.getAsJson();
   }

   public JSONObject getOne() {
      JSONArray a = this.getAsJson();
      return a.length() > 0 ? a.optJSONObject(0) : null;
   }
}
