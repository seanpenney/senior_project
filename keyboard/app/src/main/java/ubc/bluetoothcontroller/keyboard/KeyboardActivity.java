package ubc.bluetoothcontroller.keyboard;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


public class KeyboardActivity extends ActionBarActivity {

    //Set the layout to activity_bluetooth_keyboard. Should probably rename activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_keyboard);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_keyboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //The code for the Bluetooth Switch. Not strictly required for your plugin, but recommended.
    public void switchBluetooth(View view) {

        Switch ble_switch = (Switch) findViewById(R.id.bluetooth_switch);
        boolean on = ble_switch.isChecked();

        if (on) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //bluetooth adapter is not available on this device
                Context context = getApplicationContext();
                CharSequence text = "Bluetooth is not detected on this device";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                int REQUEST_ENABLE_BT = 1;
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                //bluetooth adapter is not available on this device
                Context context = getApplicationContext();
                CharSequence text = "Bluetooth is not detected on this device";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                mBluetoothAdapter.disable();
            }
        }
    }

    // This prints the message that is typed in
    public void toast_message(View view){
        Context context = getApplicationContext();
        EditText mEdit   = (EditText)findViewById(R.id.textView);
        CharSequence text = mEdit.getText().toString();
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
