package android.bluetooth;

import java.util.UUID;
import java.io.IOException;

public class BluetoothDevice {
    public String getName() {
        return "";
    }

    public String getAddress() {
        return "";
    }

    public BluetoothSocket createRfcommSocketToServiceRecord(UUID uuid) throws IOException {
        return new BluetoothSocket();
    }
}
