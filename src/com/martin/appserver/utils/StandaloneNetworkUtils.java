// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package com.martin.appserver.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class StandaloneNetworkUtils {
   public StandaloneNetworkUtils() {
   }

   public static String getLocalIp() {
      try {
         Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

         while(true) {
            NetworkInterface iface;
            do {
               do {
                  if (!interfaces.hasMoreElements()) {
                     return "localhost";
                  }

                  iface = (NetworkInterface)interfaces.nextElement();
               } while(iface.isLoopback());
            } while(!iface.isUp());

            Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while(addresses.hasMoreElements()) {
               InetAddress addr = (InetAddress)addresses.nextElement();
               if (addr.isSiteLocalAddress() && !addr.isLoopbackAddress()) {
                  String ip = addr.getHostAddress();
                  if (ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                     return ip;
                  }
               }
            }
         }
      } catch (Exception var5) {
         System.err.println("Error obteniendo IP: " + var5.getMessage());
         return "localhost";
      }
   }
}
