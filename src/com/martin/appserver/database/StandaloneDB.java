// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.database;

import com.martin.appserver.utils.ServerLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class StandaloneDB {
   private static Connection connection;

   public StandaloneDB() {
   }

   public static void initialize() {
      try {
         Class.forName("org.sqlite.JDBC");
         connection = DriverManager.getConnection("jdbc:sqlite:appserver.db");
         System.out.println("✅ Base de datos SQLite inicializada");
         ServerLogger.log("Base de datos inicializada");
         createTables();
      } catch (Exception var1) {
         System.err.println("Error inicializando DB: " + var1.getMessage());
      }

   }

   private static void createTables() {
      String[] var0 = DBSchema.getCreateTableStatements();
      int var1 = var0.length;

      for (int var2 = 0; var2 < var1; ++var2) {
         String sql = var0[var2];
         execSQL(sql);
      }

      System.out.println("✅ Tablas creadas");
   }

   private static void insertDefaultData() {
      String[] var0 = DBSchema.getDefaultDataStatements();
      int var1 = var0.length;

      for (int var2 = 0; var2 < var1; ++var2) {
         String sql = var0[var2];
         execSQL(sql);
      }

      System.out.println("✅ Datos iniciales insertados");
   }

   public static void execSQL(String sql) {
      try {
         Statement stmt = connection.createStatement();
         stmt.execute(sql);
         stmt.close();
      } catch (SQLException var2) {
         System.err.println("Error SQL: " + var2.getMessage());
      }

   }

   public static long insert(String table, Map<String, Object> values) {
      StringBuilder cols = new StringBuilder();
      StringBuilder vals = new StringBuilder();
      int count = 0;

      for (String key : values.keySet()) {
         if (count > 0) {
            cols.append(", ");
            vals.append(", ");
         }
         cols.append(key);
         vals.append("?");
         count++;
      }

      String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";

      try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         int i = 1;
         for (Object value : values.values()) {
            stmt.setObject(i++, value);
         }

         stmt.executeUpdate();
         try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
               return rs.getLong(1);
            }
         }
      } catch (SQLException e) {
         System.err.println("Error insert: " + e.getMessage());
      }
      return -1L;
   }

   public static ResultSet query(String sql) {
      try {
         Statement stmt = connection.createStatement();
         return stmt.executeQuery(sql);
      } catch (SQLException var2) {
         System.err.println("Error query: " + var2.getMessage());
         return null;
      }
   }

   public static int update(String table, Map<String, Object> values, String where) {
      StringBuilder sets = new StringBuilder();

      String key;
      for (Iterator var4 = values.keySet().iterator(); var4.hasNext(); sets.append(key).append(" = ?")) {
         key = (String) var4.next();
         if (sets.length() > 0) {
            sets.append(", ");
         }
      }

      String sql = "UPDATE " + table + " SET " + sets;
      if (where != null && !where.isEmpty()) {
         sql = sql + " WHERE " + where;
      }

      try {
         PreparedStatement stmt = connection.prepareStatement(sql);
         int i = 1;
         Iterator var7 = values.values().iterator();

         while (var7.hasNext()) {
            Object value = var7.next();
            stmt.setObject(i++, value);
         }

         return stmt.executeUpdate();
      } catch (SQLException var9) {
         System.err.println("Error update: " + var9.getMessage());
         return 0;
      }
   }

   public static int delete(String table, String where) {
      String sql = "DELETE FROM " + table;
      if (where != null && !where.isEmpty()) {
         sql = sql + " WHERE " + where;
      }

      try {
         Statement stmt = connection.createStatement();
         return stmt.executeUpdate(sql);
      } catch (SQLException var4) {
         System.err.println("Error delete: " + var4.getMessage());
         return 0;
      }
   }

   public static void close() {
      try {
         if (connection != null) {
            connection.close();
         }
      } catch (SQLException var1) {
         System.err.println("Error cerrando DB: " + var1.getMessage());
      }

   }
}
