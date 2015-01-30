package bluetoothcontroller.ubc;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View selected_item = null;
    private int offset_x = 0;
    private int offset_y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bluetooth_selector);

        View ble = findViewById(R.id.ble);
        ble.setOnLongClickListener(longListen);
        ble.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                bluetoothLowEnergy(v);;
            }
        });

        View bc_gamepad = findViewById(R.id.bc_gamepad);
        bc_gamepad.setOnLongClickListener(longListen);
        bc_gamepad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                bluetoothGamepad(v);
            }
        });

        View bc_keyboard = findViewById(R.id.bc_keyboard);
        bc_keyboard.setOnLongClickListener(longListen);
        bc_keyboard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                bluetoothKeyboard(v);
            }
        });
        findViewById(R.id.drop_view).setOnDragListener(DropListener);

    }

    public void bluetoothLowEnergy(View view) {
        Intent intent = new Intent(this, LowEnergyActivity.class);
        startActivity(intent);
    }

    public void bluetoothKeyboard(View view) {
        Intent intent = new Intent(this, KeyboardActivity.class);
        startActivity(intent);
    }

    public void bluetoothGamepad(View view) {
        Intent intent = new Intent(this, GamepadActivity.class);
        startActivity(intent);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_bluetooth_keyboard, container, false);
            return rootView;
        }



    }

    View.OnDragListener DropListener = new View.OnDragListener(){

            @Override
            public boolean onDrag(View v, DragEvent event){
                int dragEvent = event.getAction();

                switch(dragEvent){
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i("Drag Event", "Entered");
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i("Drag Event", "Exited");
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Button target = (Button) new Button(getApplicationContext());
                        final Button dragged = (Button) event.getLocalState();
                        target.setText(dragged.getText());
                        LinearLayout layout = (LinearLayout) findViewById(R.id.drop_view);
                        layout.addView(target);
                        target.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                dragged.callOnClick();
                        }
                      });
                        break;
                }
                return true;
            }
    };

    View.OnLongClickListener longListen = new View.OnLongClickListener(){

        @Override
        public boolean onLongClick(View v){
            ClipData data = ClipData.newPlainText("", "");
            DragShadow dragShadow = new DragShadow(v);

            v.startDrag(data, dragShadow, v, 0);
            return false;
        }
    };

    private class DragShadow extends View.DragShadowBuilder {

        ColorDrawable greyBox;

        public DragShadow(View view) {
            super(view);
            greyBox = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            greyBox.draw(canvas);
        }

        @Override
        public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
            View v = getView();

            int height = (int)v.getHeight();
            int width = (int)v.getWidth();

            greyBox.setBounds(0, 0, width, height);
            shadowSize.set(width, height);
            shadowTouchPoint.set((int)width/2,(int)height/2);
        }
    }
}


