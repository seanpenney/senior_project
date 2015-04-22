package ubc.bluetoothcontroller.garduino;

import java.util.HashMap;

/**
 * Created by sean on 1/20/2015.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String TX_CHARACTERISTIC = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static String RX_CHARACTERISTIC = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static String UART_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "UART Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("6e400003-b5a3-f393-e0a9-e50e24dcca9e", "TX Characteristic");
        attributes.put("6e400002-b5a3-f393-e0a9-e50e24dcca9e", "RX Characteristic");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}