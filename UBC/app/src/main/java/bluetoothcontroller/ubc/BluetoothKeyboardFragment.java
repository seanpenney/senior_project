package bluetoothcontroller.ubc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Aaron on 1/10/2015.
 */
public class BluetoothKeyboardFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setRetainInstance(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bluetooth_keyboard,
                container, false);
        return view;
    }
}