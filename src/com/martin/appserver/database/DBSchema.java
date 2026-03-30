package com.martin.appserver.database;

public class DBSchema {
      public DBSchema() {
      }

      public static String[] getCreateTableStatements() {
            return new String[] {
                        "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, whatsapp TEXT, password TEXT, role TEXT NOT NULL, status TEXT NOT NULL, avatar TEXT, created_at INTEGER NOT NULL)",
                        "CREATE TABLE IF NOT EXISTS company_config (id INTEGER PRIMARY KEY CHECK (id = 1), company_name TEXT, username TEXT, pin TEXT, primary_color TEXT, updated_at INTEGER)",
                        "CREATE TABLE IF NOT EXISTS shifts (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, opened_at INTEGER NOT NULL, closed_at INTEGER, opening_amount REAL NOT NULL, closing_amount REAL, status TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES users(id))",
                        "CREATE TABLE IF NOT EXISTS categories (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, icon TEXT, created_at INTEGER NOT NULL)",

                        // New Inventory Tables
                        "CREATE TABLE IF NOT EXISTS inventory_items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, unit TEXT NOT NULL, min_stock_alert INTEGER DEFAULT 0, created_at INTEGER NOT NULL)",
                        "CREATE TABLE IF NOT EXISTS inventory_movements (id INTEGER PRIMARY KEY AUTOINCREMENT, inventory_item_id INTEGER NOT NULL, type TEXT NOT NULL CHECK (type IN ('sale', 'purchase', 'adjustment')), quantity REAL NOT NULL, reference_id INTEGER, created_at INTEGER NOT NULL, FOREIGN KEY(inventory_item_id) REFERENCES inventory_items(id))",

                        // New Products Table
                        "CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT, price REAL NOT NULL, purchase_price REAL, type TEXT NOT NULL CHECK (type IN ('simple', 'composed')), inventory_item_id INTEGER, status TEXT DEFAULT 'active', images TEXT, created_at INTEGER NOT NULL, FOREIGN KEY(inventory_item_id) REFERENCES inventory_items(id))",
                        "CREATE TABLE IF NOT EXISTS product_recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, product_id INTEGER NOT NULL, inventory_item_id INTEGER NOT NULL, quantity_required REAL NOT NULL, FOREIGN KEY(product_id) REFERENCES products(id), FOREIGN KEY(inventory_item_id) REFERENCES inventory_items(id))",

                        "CREATE TABLE IF NOT EXISTS product_categories (product_id INTEGER, category_id INTEGER, PRIMARY KEY(product_id, category_id), FOREIGN KEY(product_id) REFERENCES products(id), FOREIGN KEY(category_id) REFERENCES categories(id))",
                        "CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, shift_id INTEGER NOT NULL, customer_name TEXT, total REAL NOT NULL, payment_method TEXT, status TEXT NOT NULL, created_at INTEGER NOT NULL, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(shift_id) REFERENCES shifts(id))",
                        "CREATE TABLE IF NOT EXISTS order_items (id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER NOT NULL, product_id INTEGER NOT NULL, quantity INTEGER NOT NULL, price REAL NOT NULL, subtotal REAL NOT NULL, FOREIGN KEY(order_id) REFERENCES orders(id), FOREIGN KEY(product_id) REFERENCES products(id))",
                        "CREATE TABLE IF NOT EXISTS order_payments (id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER NOT NULL, payment_method TEXT NOT NULL, amount REAL NOT NULL, FOREIGN KEY(order_id) REFERENCES orders(id))"
            };
      }

}
