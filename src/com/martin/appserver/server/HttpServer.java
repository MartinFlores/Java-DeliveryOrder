// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.server;

import com.martin.appserver.utils.Log;
import java.io.File;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
   private ServerSocket serverSocket;
   private boolean isRunning = false;
   private Thread serverThread;
   private File wwwRoot;

   public HttpServer(File wwwRoot) {
      this.wwwRoot = wwwRoot;
   }

   public void start() {
      if (!this.isRunning) {
         this.isRunning = true;
         this.serverThread = new Thread(new Runnable() {
            public void run() {
               HttpServer.this.runServer();
            }
         });
         this.serverThread.start();
      }
   }

   public void stop() {
      this.isRunning = false;

      try {
         if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            this.serverSocket.close();
         }
      } catch (Exception var2) {
         Log.e("AppServer", "Error al cerrar servidor: " + var2.getMessage());
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }

   private void runServer() {
      try {
         this.serverSocket = new ServerSocket(7979, 50, (InetAddress) null);
         Log.i("AppServer", "Servidor iniciado en puerto 7979 (todas las interfaces)");

         while (this.isRunning) {
            try {
               Socket client = this.serverSocket.accept();
               (new Thread(new ClientHandler(client, this.wwwRoot))).start();
            } catch (Exception var11) {
               if (this.isRunning) {
                  Log.e("AppServer", "Error aceptando cliente: " + var11.getMessage());
               }
            }
         }
      } catch (Exception var12) {
         Log.e("AppServer", "Error en servidor: " + var12.getMessage());
      } finally {
         try {
            if (this.serverSocket != null && !this.serverSocket.isClosed()) {
               this.serverSocket.close();
            }
         } catch (Exception var10) {
            Log.e("AppServer", "Error cerrando servidor: " + var10.getMessage());
         }

      }

   }
}
