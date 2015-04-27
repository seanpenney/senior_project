package ubc.bluetoothcontroller.ecearduinoplugin;

import java.util.HashMap;

/**
 * Created by sean on 1/20/2015.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String RX_CHARACTERISTIC = "713d0003-503e-4c75-ba94-3148f18d941e";
    public static String TX_CHARACTERISTIC = "713d0002-503e-4c75-ba94-3148f18d941e";
    public static String UART_SERVICE = "713d0000-503e-4c75-ba94-3148f18d941e";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services
        attributes.put("713d0000-503e-4c75-ba94-3148f18d941e", "UART Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put("713d0003-503e-4c75-ba94-3148f18d941e", "RX Characteristic");
        attributes.put("713d0002-503e-4c75-ba94-3148f18d941e", "TX Characteristic");

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}