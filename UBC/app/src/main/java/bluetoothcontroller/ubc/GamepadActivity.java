package bluetoothcontroller.ubc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bda.controller.Controller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Aaron on 1/14/2015.
 * Modified by Paul on 1/28/2015
 */
public class GamepadActivity extends ActionBarActivity {

    Controller mController = null;
    TextView buttonTextView;
    boolean isRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_gamepad);

        mController = Controller.getInstance(this);
        mController.init();

        buttonTextView = (TextView) findViewById(R.id.textView);
        buttonTextView.setText("Press OK to start");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void toast_message(View view){

        //myThread.start();
        /*int period = 1000;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, period, period);*/
        update();

    }
    /*Thread myThread = new Thread() {
        public void run() {
            update();
        }
    };*/

    Thread myThread = new Thread() {
        public void run() {
            update();
        }
    };



    @Override
    protected void onDestroy() {
        if(mController != null) {
            mController.exit();
        }
        super.onDestroy();
    }

    public void run() {
        isRunning = false;
        //update();
    }

    public void update() {
        Log.v("Debug", "BOOOOP");
        if(mController != null) {
            if(mController.getState(Controller.STATE_CONNECTION) == Controller.ACTION_CONNECTED) {
                if(mController.getKeyCode(Controller.KEYCODE_BUTTON_A) == Controller.ACTION_DOWN) {
                    // button A is pressed
                    buttonTextView.setText("A being pressed");
                    Log.v("Debug", "A");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_B) == Controller.ACTION_DOWN) {
                    // button B is pressed
                    buttonTextView.setText("B being pressed");
                    Log.v("Debug", "B");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_X) == Controller.ACTION_DOWN) {
                    // button X is pressed
                    buttonTextView.setText("X being pressed");
                    Log.v("Debug", "X");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_Y) == Controller.ACTION_DOWN) {
                    // button Y is pressed
                    buttonTextView.setText("Y being pressed");
                    Log.v("Debug", "Y");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_SELECT) == Controller.ACTION_DOWN) {
                    // button Select is pressed
                    buttonTextView.setText("Select being pressed");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_START) == Controller.ACTION_DOWN) {
                    // button Start is pressed
                    buttonTextView.setText("Start being pressed");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_L1) == Controller.ACTION_DOWN) {
                    // button Left Bumper is pressed
                    buttonTextView.setText("Left Bumper being pressed");
                } else if(mController.getKeyCode(Controller.KEYCODE_BUTTON_R1) == Controller.ACTION_DOWN) {
                    // button Right Bumper is pressed
                    buttonTextView.setText("Right Bumper being pressed");
                } else {
                    Log.v("Debug", "NONE");
                    // No button is currently being pressed
                    buttonTextView.setText("No buttons are being pressed");
                }
            }
        } else {
            buttonTextView.setText("Controller Not Connected");
        }
        isRunning = false;
    }



}

