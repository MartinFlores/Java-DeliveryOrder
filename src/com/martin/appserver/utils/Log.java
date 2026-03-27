package com.martin.appserver.utils;

public class Log {
    public static void i(String tag, String msg) {
        System.out.println("[" + tag + "] INFO: " + msg);
        ServerLogger.log(tag + ": " + msg);
    }

    public static void e(String tag, String msg) {
        System.err.println("[" + tag + "] ERROR: " + msg);
        ServerLogger.log("ERROR [" + tag + "]: " + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        System.err.println("[" + tag + "] ERROR: " + msg);
        if (tr != null)
            tr.printStackTrace();
        ServerLogger.log("ERROR [" + tag + "]: " + msg + (tr != null ? " - " + tr.getMessage() : ""));
    }

    public static void w(String tag, String msg) {
        System.out.println("[" + tag + "] WARN: " + msg);
        ServerLogger.log("WARN [" + tag + "]: " + msg);
    }

    public static void d(String tag, String msg) {
        System.out.println("[" + tag + "] DEBUG: " + msg);
    }
}
