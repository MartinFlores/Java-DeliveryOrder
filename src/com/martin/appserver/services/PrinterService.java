package com.martin.appserver.services;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.utils.ServerLogger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class PrinterService {
    private static final String SETTINGS_KEY = "printer_config";

    public static JSONArray listPairedPrinters() {
        JSONArray array = new JSONArray();
        ServerLogger.log("Standalone mode: no Bluetooth printers available");
        return array;
    }

    public static void savePrinter(JSONObject printerConfig) {
        try {
            DBAdapter.execSQL("INSERT OR REPLACE INTO settings (key, value) VALUES ('"
                    + SETTINGS_KEY + "', '" + printerConfig.toString().replace("'", "''") + "')");
        } catch (Exception e) {
            ServerLogger.log("Error saving printer config: " + e.getMessage());
        }
    }

    public static JSONObject getSavedPrinter() {
        try {
            List<Map<String, Object>> results = DBAdapter
                    .query("SELECT value FROM settings WHERE key = '" + SETTINGS_KEY + "'");
            if (results != null && !results.isEmpty()) {
                return new JSONObject((String) results.get(0).get("value"));
            }
        } catch (Exception e) {
            ServerLogger.log("Error getting saved printer: " + e.getMessage());
        }
        return null;
    }

    public static boolean sendBytes(byte[] bytes) {
        JSONObject config = getSavedPrinter();
        if (config == null) return false;

        String ip = config.optString("ip");
        int port = config.optInt("port", 9100);

        if (ip.isEmpty()) {
            ServerLogger.log("No printer IP configured");
            return false;
        }

        try (Socket socket = new Socket(ip, port);
             OutputStream out = socket.getOutputStream()) {
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            ServerLogger.log("Error printing to " + ip + ":" + port + " - " + e.getMessage());
            return false;
        }
    }
}
