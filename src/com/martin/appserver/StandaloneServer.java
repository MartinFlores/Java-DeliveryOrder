package com.martin.appserver;

import com.martin.appserver.database.DBAdapter;
import com.martin.appserver.routing.RouteRegistry;
import com.martin.appserver.routing.Router;
import com.martin.appserver.server.ClientHandler;
import com.martin.appserver.utils.ServerLogger;
import com.martin.appserver.utils.StandaloneNetworkUtils;
import com.martin.appserver.database.StandaloneDB;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import javax.imageio.ImageIO;

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
        Image icon = loadIcon();

        PopupMenu popup = new PopupMenu();

        MenuItem infoItem = new MenuItem("DeliveryOrder - " + localIp + ":" + PORT);
        infoItem.setEnabled(false);
        popup.add(infoItem);

        popup.addSeparator();

        MenuItem openItem = new MenuItem("Abrir en navegador");
        openItem.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new URI(url)); }
            catch (Exception ex) { ServerLogger.log("Error abriendo browser: " + ex.getMessage()); }
        });
        popup.add(openItem);

        MenuItem openDataItem = new MenuItem("Abrir ubicación de la data");
        openDataItem.addActionListener(e -> {
            try { Desktop.getDesktop().open(new File(StandaloneDB.getDataDir())); }
            catch (Exception ex) { ServerLogger.log("Error abriendo carpeta data: " + ex.getMessage()); }
        });
        popup.add(openDataItem);
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

        TrayIcon trayIcon = new TrayIcon(icon, "DeliveryOrder - " + localIp + ":" + PORT, popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("DeliveryOrder corriendo en " + url);

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage("DeliveryOrder iniciado", "Escuchando en " + url, TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            ServerLogger.log("No se pudo agregar tray icon: " + e.getMessage());
        }
    }

    private static Image loadIcon() {
        try {
            return ImageIO.read(new File("icon-server.png"));
        } catch (IOException e) {
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(new Color(34, 197, 94));
            g.fillOval(0, 0, 16, 16);
            g.dispose();
            return img;
        }
    }

    private static void startServer(String wwwRootPath) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT, 50, (InetAddress) null);
            launchAdminApp();
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

    private static void launchAdminApp() {
        try {
            File jarDir = new File(StandaloneServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File adminExe = new File(jarDir, "ADMIN.exe");
            if (!adminExe.exists()) adminExe = new File(jarDir.getParentFile(), "ADMIN.exe");
            if (adminExe.exists()) {
                new ProcessBuilder(adminExe.getAbsolutePath())
                    .directory(adminExe.getParentFile())
                    .start();
                ServerLogger.log("ADMIN.exe iniciado desde: " + adminExe.getAbsolutePath());
            } else {
                ServerLogger.log("ADMIN.exe no encontrado en: " + jarDir);
            }
        } catch (Exception e) {
            ServerLogger.log("Error iniciando ADMIN.exe: " + e.getMessage());
        }
    }
}
