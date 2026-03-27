package com.martin.appserver;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.routing.RouteRegistry;
import com.martin.appserver.routing.Router;
import com.martin.appserver.server.ClientHandler;
import com.martin.appserver.utils.ServerLogger;
import com.martin.appserver.utils.StandaloneNetworkUtils;
import com.martin.appserver.database.StandaloneDB;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;

public class StandaloneServer {
    private static final int PORT = 7979;
    private static final String WWW_ROOT = "www";

    public static void main(String[] args) {
        String wwwRootPath = args.length > 0 ? args[0] : WWW_ROOT;

        DBAdapter.initialize((Object) null);
        RouteRegistry.register();

        String localIp = StandaloneNetworkUtils.getLocalIp();
        String url = "http://" + localIp + ":" + PORT;

        ServerLogger.log("Servidor iniciado en puerto " + PORT);
        ServerLogger.log("Rutas registradas: " + Router.count());

        setupTray(localIp, url);
        startServer(wwwRootPath);
    }

    private static void setupTray(String localIp, String url) {
        if (!SystemTray.isSupported()) return;

        SystemTray tray = SystemTray.getSystemTray();
        Image icon = createDefaultIcon();

        PopupMenu popup = new PopupMenu();

        MenuItem infoItem = new MenuItem("POS Server - " + localIp + ":" + PORT);
        infoItem.setEnabled(false);
        popup.add(infoItem);

        popup.addSeparator();

        MenuItem openItem = new MenuItem("Abrir en navegador");
        openItem.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new URI(url)); }
            catch (Exception ex) { ServerLogger.log("Error abriendo browser: " + ex.getMessage()); }
        });
        popup.add(openItem);

        MenuItem copyItem = new MenuItem("Copiar URL: " + url);
        copyItem.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new java.awt.datatransfer.StringSelection(url), null);
        });
        popup.add(copyItem);

        popup.addSeparator();

        MenuItem exitItem = new MenuItem("Detener servidor");
        exitItem.addActionListener(e -> {
            StandaloneDB.close();
            System.exit(0);
        });
        popup.add(exitItem);

        TrayIcon trayIcon = new TrayIcon(icon, "POS Server - " + localIp + ":" + PORT, popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("POS Server corriendo en " + url);

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("POS Server iniciado", "Escuchando en " + url, TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            ServerLogger.log("No se pudo agregar tray icon: " + e.getMessage());
        }
    }

    private static Image createDefaultIcon() {
        // 16x16 green square as fallback icon
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(34, 197, 94));
        g.fillOval(0, 0, 16, 16);
        g.dispose();
        return img;
    }

    private static void startServer(String wwwRootPath) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT, 50, (InetAddress) null);
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(new ClientHandler(client, new File(wwwRootPath))).start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            StandaloneDB.close();
        }
    }
}
