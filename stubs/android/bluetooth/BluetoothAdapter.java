package android.bluetooth;

import java.util.Set;
import java.util.HashSet;

public class BluetoothAdapter {
    public static BluetoothAdapter getDefaultAdapter() {
        return new BluetoothAdapter();
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return new HashSet<>();
    }

    public boolean isEnabled() {
        return false;
    }

    public BluetoothDevice getRemoteDevice(String address) {
        return new BluetoothDevice();
    }
}
