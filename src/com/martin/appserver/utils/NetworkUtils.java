package com.martin.appserver.utils;

import com.martin.appserver.utils.Log;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtils {
    public NetworkUtils() {
    }

    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (true) {
                NetworkInterface iface;
                do {
                    do {
                        if (!interfaces.hasMoreElements()) {
                            return "127.0.0.1";
                        }

                        iface = (NetworkInterface) interfaces.nextElement();
                    } while (iface.isLoopback());
                } while (!iface.isUp());

                Enumeration<InetAddress> addresses = iface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = (InetAddress) addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception var4) {
            Log.e("AppServer", "Error al obtener IP: " + var4.getMessage());
            return "127.0.0.1";
        }
    }
}
