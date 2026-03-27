package com.martin.appserver.web;

import com.martin.appserver.utils.Log;
import java.io.File;

public class WebAppManager {

    public static boolean webAppExists(String wwwPath) {
        File index = new File(wwwPath, "index.html");
        return index.exists();
    }

    public static String getLastErrorMessage() {
        return null;
    }
}
