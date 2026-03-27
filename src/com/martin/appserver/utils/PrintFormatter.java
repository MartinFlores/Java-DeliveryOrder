package com.martin.appserver.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PrintFormatter {
    private static final byte ESC = 0x1B;
    private static final byte GS = 0x1D;
    private static final byte LF = 0x0A;

    public static byte[] alignCenter() {
        return new byte[] { ESC, 0x61, 0x01 };
    }

    public static byte[] alignLeft() {
        return new byte[] { ESC, 0x61, 0x00 };
    }

    public static byte[] alignRight() {
        return new byte[] { ESC, 0x61, 0x02 };
    }

    public static byte[] setBold(boolean bold) {
        return new byte[] { ESC, 0x45, (byte) (bold ? 0x01 : 0x00) };
    }

    public static byte[] setDoubleSize(boolean doubleHeight, boolean doubleWidth) {
        int size = 0;
        if (doubleHeight)
            size |= 0x10;
        if (doubleWidth)
            size |= 0x20;
        return new byte[] { GS, 0x21, (byte) size };
    }

    public static byte[] cutPaper() {
        return new byte[] { GS, 0x56, 0x42, 0x00 };
    }

    public static byte[] lineBreak() {
        return new byte[] { LF };
    }

    public static byte[] setCharset(String charset) {
        // CP850 is often n=2 or n=3 or n=13 depending on the printer
        // Most common for CP850 is 2
        return new byte[] { ESC, 0x74, 0x02 };
    }

    public static byte[] generateTestTicket(int paperWidth) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(alignCenter());
            stream.write(setDoubleSize(true, true));
            stream.write(setBold(true));
            stream.write("TICKET DE PRUEBA\n".getBytes("CP850"));

            stream.write(setDoubleSize(false, false));
            stream.write(setBold(false));
            stream.write(lineBreak());
            stream.write("==============================\n".getBytes("CP850"));
            stream.write("Si puede leer esto, su\n".getBytes("CP850"));
            stream.write("impresora esta configurada\n".getBytes("CP850"));
            stream.write("correctamente.\n".getBytes("CP850"));
            stream.write(lineBreak());
            stream.write("Ancho: ".getBytes("CP850"));
            stream.write(String.valueOf(paperWidth).getBytes("CP850"));
            stream.write("mm\n".getBytes("CP850"));
            stream.write("==============================\n".getBytes("CP850"));
            stream.write(lineBreak());
            stream.write(lineBreak());
            stream.write(lineBreak());
            stream.write(cutPaper());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }
}
