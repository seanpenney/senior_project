package bluetoothcontroller.ubc;

import android.app.ActionBar;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemLongClickListener {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private View selected_item = null;
    private int offset_x = 0;
    private int offset_y = 0;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] plugins;
    public ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bluetooth_selector);


        mPlanetTitles = getResources().getStringArray(R.array.plugins);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.drawerList);
        plugins = getResources().getStringArray(R.array.plugins);
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, plugins));
        // Set the list's click listener

        mDrawerList.setOnItemLongClickListener(this);

        findViewById(R.id.drop_view1).setOnDragListener(DropListener);
        findViewById(R.id.drop_view2).setOnDragListener(DropListener);
        findViewById(R.id.drop_view3).setOnDragListener(DropListener);
        findViewById(R.id.drop_view4).setOnDragListener(DropListener);
        findViewById(R.id.delete_drop_view).setOnDragListener(DropListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                   long id) {
        ClipData data = ClipData.newPlainText("", "");
        DragShadow dragShadow = new DragShadow(view);

        view.startDrag(data, dragShadow, view, 0);
        return false;
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
                        //v.setBackgroundColor(R.color.accent_material_dark);
                        v.invalidate();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        //v.setBackgroundColor(R.color.bright_foreground_material_dark);
                        v.invalidate();
                        Log.i("Drag Event", "Exited");
                        break;
                    case DragEvent.ACTION_DROP:
                            View cView = (View) event.getLocalState();
                            Button target = (Button) new Button(getApplicationContext());
                            final TextView dragged = (TextView) event.getLocalState();
                            final String text = dragged.getText().toString();
                            target.setText(dragged.getText());
                            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat
                                    .LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                            LinearLayout layout;

                        if (v.getId() == R.id.delete_drop_view) {
                                layout = (LinearLayout) cView.getParent();
                                layout.removeView(dragged);
                            }
                        else if(v.getId() == R.id.drop_view1 || v.getId() == R.id.drop_view2 || v.getId() == R.id.drop_view3 ||v.getId() == R.id.drop_view4) {
                            layout = (LinearLayout) findViewById(v.getId());
                            layout.addView(target, lp);
                            target.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (text.equals(getString(R.string.bc_keyboard))) {
                                        bluetoothKeyboard(v);
                                    } else if (text.equals(getString(R.string.bc_gamePad))) {
                                        bluetoothLowEnergy(v);
                                    } else if (text.equals(getString(R.string.ble))) {
                                        bluetoothLowEnergy(v);
                                    }
                                }
                            });
                            target.setOnLongClickListener(longListen);
                        }
                        else Log.i("Who done Messed up", "You did!" );
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


