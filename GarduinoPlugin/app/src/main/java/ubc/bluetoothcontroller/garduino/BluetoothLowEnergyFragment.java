package ubc.bluetoothcontroller.garduino;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by sean on 1/19/2015.
 */
public class BluetoothLowEnergyFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    Hashtable<String, BluetoothDevice> devices = new Hashtable<String, BluetoothDevice>();
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    // Stops scanning after 5 seconds.
    private static final long SCAN_PERIOD = 5000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth_low_energy,
                container, false);
        Button button = (Button) view.findViewById(R.id.scan_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Scan Button Clicked", Toast.LENGTH_SHORT).show();
                BleScan();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            ;
            return;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        listItems.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    public void BleScan() {
        scanLeDevice(true);
    }

    public void connectTo(BluetoothDevice device) {
        scanLeDevice(false);
        String name = device.getName();
        String address = device.getAddress();

        Log.d("lowenergyfragment", "Called connectTo for " + name + " " + address);

        Intent intent = new Intent("CONNECT");
        intent.putExtra("NAME", name);
        intent.putExtra("ADDRESS", address);
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getActivity());
        mgr.sendBroadcast(intent);

    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Found " + device.getName() + ",\tAddress: " + device.getAddress(), Toast.LENGTH_SHORT).show();
                            if (device.getName() != null && device.getAddress() != null && devices.get(device.getName()) == null) {
                                devices.put(device.getName(), device);
                                listItems.add(device.getName());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        connectTo(devices.get(parent.getAdapter().getItem(position)));
    }
}