package bluetoothcontroller.ubc;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by sean on 1/20/2015, using Android developer guide as reference
 * Receive data from Arduino on the Tx characteristics, and send on Rx characteristic
 */

public class BleDeviceControl extends Fragment {
    private final static String TAG = BleDeviceControl.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "ADDRESS";

    public final static UUID UUID_RX_CHARACTERISTIC =
            UUID.fromString(SampleGattAttributes.RX_CHARACTERISTIC);

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                setupNotification();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    public void setupNotification() {
        BluetoothGattCharacteristic characteristic = mBluetoothLeService.returnTxCharacteristic();
        mNotifyCharacteristic = characteristic;
        mBluetoothLeService.setCharacteristicNotification(characteristic, true);

    }

    private void clearUI() {
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        String name = bundle.getString("NAME");
        String address = bundle.getString("ADDRESS");
        Log.d("bledevicecontrol", "received" + name + address);

        mDeviceName = name;
        mDeviceAddress = address;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ble_arduino_control,
                container, false);

        ((TextView) view.findViewById(R.id.device_address)).setText(mDeviceAddress);
        ((TextView) view.findViewById(R.id.device_name)).setText(mDeviceName);
        mConnectionState = (TextView) view.findViewById(R.id.connection_state);
        mDataField = (TextView) view.findViewById(R.id.data_value);

        /*
        Button button = (Button) view.findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Sent text to Arduino", Toast.LENGTH_SHORT).show();
                sendToArduino();
            }
        });
        */

        Button get_pin_val_button = (Button) view.findViewById(R.id.get_pin_val_button);
        get_pin_val_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPinVal();
            }
        });

        Button set_pin_val_button = (Button) view.findViewById(R.id.set_pin_val_button);
        set_pin_val_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPinVal();
            }
        });

        Button set_pin_high_button = (Button) view.findViewById(R.id.pin_high_button);
        set_pin_high_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPinHigh();
            }
        });

        Button set_pin_low_button = (Button) view.findViewById(R.id.pin_low_button);
        set_pin_low_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPinLow();
            }
        });

        Button setup_timer_button = (Button) view.findViewById(R.id.timer_button);
        setup_timer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupTimer();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "about to register");

        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED));
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_DISCONNECTED));
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED));
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE));
        getActivity().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        Log.d(TAG, "registered");

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED));
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_DISCONNECTED));
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED));
        getActivity().registerReceiver(mGattUpdateReceiver, new IntentFilter(BluetoothLeService.ACTION_DATA_AVAILABLE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
    }

    private void updateConnectionState(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mDataField.setText(data);
        }
    }

    /*
    private void sendToArduino() {
        EditText input = (EditText) getActivity().findViewById(R.id.submit_text);
        String message = input.getText().toString();
        byte[] value = new byte[0];
        try {
            value = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeRXCharacteristic(value);
    }
    */

    private void getPinVal() {
        EditText input = (EditText) getActivity().findViewById(R.id.get_pin_val);
        String message = "<get_pinval>" + input.getText().toString();
        byte[] value = new byte[0];
        try {
            value = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeRXCharacteristic(value);
    }

    private void setPinVal() {
        EditText pinInput = (EditText) getActivity().findViewById(R.id.set_pin);
        int pinNumber = Integer.parseInt(pinInput.getText().toString());

        EditText valInput = (EditText) getActivity().findViewById(R.id.set_val);
        int valNumber = Integer.parseInt(valInput.getText().toString());

        String message = "<set_pinval>" + String.format("%02d", pinNumber) + String.format("%03d", valNumber);
        byte[] value = new byte[0];
        try {
            value = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeRXCharacteristic(value);
    }

    private void setPinHigh() {
        EditText pinInput = (EditText) getActivity().findViewById(R.id.pin_high);
        int pinNumber = Integer.parseInt(pinInput.getText().toString());

        String message = "<pin_high>" + String.format("%02d", pinNumber);
        byte[] value = new byte[0];
        try {
            value = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeRXCharacteristic(value);
    }

    private void setPinLow() {
        EditText pinInput = (EditText) getActivity().findViewById(R.id.pin_low);
        int pinNumber = Integer.parseInt(pinInput.getText().toString());

        String message = "<pin_low>" + String.format("%02d", pinNumber);
        byte[] value = new byte[0];
        try {
            value = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeRXCharacteristic(value);
    }

    private void setupTimer() {
        EditText timerInput = (EditText) getActivity().findViewById(R.id.timer_value);
        int timerValue = Integer.parseInt(timerInput.getText().toString());

        String message = "<timer>" + String.format("%03d", timerValue);
        byte[] value = new byte[0];
        try {
            value = message.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mBluetoothLeService.writeRXCharacteristic(value);
        Toast.makeText(getActivity(), "Time set, now setup action to perform", Toast.LENGTH_LONG).show();
    }

}
