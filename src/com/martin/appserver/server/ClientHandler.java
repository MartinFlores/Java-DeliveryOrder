// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.server;

import com.martin.appserver.utils.Log;
import com.martin.appserver.routing.Route;
import com.martin.appserver.routing.Router;
import com.martin.appserver.utils.FileUtils;
import com.martin.appserver.utils.NetworkUtils;
import com.martin.appserver.utils.QueryParser;
import com.martin.appserver.utils.ServerLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket client;
    private File wwwRoot;

    public ClientHandler(Socket client, File wwwRoot) {
        this.client = client;
        this.wwwRoot = wwwRoot;
    }

    public void run() {
        this.handleClient();
    }

    private void handleClient() {
        try {
            InputStream rawIn = this.client.getInputStream();
            StringBuilder headerBuilder = new StringBuilder();

            int b;
            while ((b = rawIn.read()) != -1) {
                headerBuilder.append((char) b);
                if (headerBuilder.toString().endsWith("\r\n\r\n")) {
                    break;
                }
            }

            String fullHeaders = headerBuilder.toString();
            if (fullHeaders.isEmpty()) {
                this.client.close();
                return;
            }

            String[] lines = fullHeaders.split("\r\n");
            String requestLine = lines[0];
            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                this.client.close();
                return;
            }

            String method = parts[0];
            String path = parts[1];
            int contentLength = 0;
            String[] var11 = lines;
            int var12 = lines.length;

            int totalRead;
            for (totalRead = 0; totalRead < var12; ++totalRead) {
                String line = var11[totalRead];
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.substring(15).trim());
                }
            }

            String body = "";
            if (contentLength > 0) {
                byte[] bodyBytes = new byte[contentLength];

                int read;
                for (totalRead = 0; totalRead < contentLength; totalRead += read) {
                    read = rawIn.read(bodyBytes, totalRead, contentLength - totalRead);
                    if (read == -1) {
                        break;
                    }
                }

                body = new String(bodyBytes, "UTF-8");
            }

            ServerLogger.log(method + " " + path);
            if ("OPTIONS".equals(method)) {
                this.handleOptionsRequest();
                return;
            }

            this.handleRequest(method, path, body);
        } catch (Exception var16) {
            Log.e("AppServer", "Error handleClient: " + var16.getMessage());
            ServerLogger.log("ERROR: " + var16.getMessage());

            try {
                this.client.close();
            } catch (Exception var15) {
            }
        }

    }

    private void handleRequest(String method, String path, String body) throws Exception {
        OutputStream out = this.client.getOutputStream();
        PrintWriter writer = new PrintWriter(out, false);
        String cleanPath = QueryParser.getPathWithoutQuery(path);
        Map<String, String> queryParams = QueryParser.parse(path);
        Route route = Router.find(method, cleanPath);
        String response;
        if (route != null) {
            response = Router.execute(route, queryParams, body);
            if (response != null) {
                this.sendJsonResponse(writer, response);
                this.client.close();
                return;
            }
        }

        response = FileUtils.normalizePath(cleanPath);
        String fileCleanPath = FileUtils.cleanPath(response);
        File file = new File(this.wwwRoot, fileCleanPath);
        if (FileUtils.isValidFile(file)) {
            this.serveFile(writer, out, file);
        } else {
            this.sendError404(writer, path);
        }

        this.client.close();
    }

    private void handleOptionsRequest() throws Exception {
        OutputStream out = this.client.getOutputStream();
        PrintWriter writer = new PrintWriter(out, false);
        writer.print("HTTP/1.1 204 No Content\r\n");
        writer.print("Access-Control-Allow-Origin: *\r\n");
        writer.print("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n");
        writer.print("Access-Control-Allow-Headers: Content-Type\r\n");
        writer.print("\r\n");
        writer.flush();
        this.client.close();
    }

    private void sendJsonResponse(PrintWriter writer, String content) {
        byte[] contentBytes = content.getBytes();
        writer.print("HTTP/1.1 200 OK\r\n");
        writer.print("Content-Type: application/json; charset=UTF-8\r\n");
        writer.print("Content-Length: " + contentBytes.length + "\r\n");
        writer.print("Access-Control-Allow-Origin: *\r\n");
        writer.print("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n");
        writer.print("Access-Control-Allow-Headers: Content-Type\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.print(content);
        writer.flush();
    }

    private void serveFile(PrintWriter writer, OutputStream out, File file) throws Exception {
        String mimeType = FileUtils.getMimeType(file.getAbsolutePath());
        writer.print("HTTP/1.1 200 OK\r\n");
        writer.print("Content-Type: " + mimeType + "\r\n");
        writer.print("Content-Length: " + file.length() + "\r\n");
        writer.print("\r\n");
        writer.flush();
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[8192];

        int n;
        while ((n = fis.read(buffer)) > 0) {
            out.write(buffer, 0, n);
        }

        out.flush();
        fis.close();
    }

    private void sendError404(PrintWriter writer, String path) {
        // SPA fallback: cualquier ruta no-API devuelve index.html para Vue Router
        File index = new File(this.wwwRoot, "index.html");
        if (index.exists()) {
            try {
                serveFile(writer, this.client.getOutputStream(), index);
            } catch (Exception e) {
                writer.print("HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n");
                writer.print("<h1>404 Not Found</h1><p>" + path + "</p>");
                writer.flush();
            }
        } else {
            writer.print("HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n");
            writer.print("<h1>Error: frontend no encontrado</h1><p>Ruta: " + path + "</p>");
            writer.flush();
        }
    }
}
