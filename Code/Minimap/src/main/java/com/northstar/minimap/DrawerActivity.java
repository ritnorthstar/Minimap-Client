package com.northstar.minimap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DrawerActivity extends FragmentActivity {


    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private ListView leftDrawerListView;
    private ListView rightDrawerListView;
    private String[] leftDrawerItems;
    private String[] rightDrawerItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        initBluetooth();
        initDrawers();
    }

    private void initDrawers() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        leftDrawerItems = getResources().getStringArray(R.array.left_drawer_items);
        leftDrawerListView = (ListView) findViewById(R.id.left_drawer);
        leftDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, leftDrawerItems));
        leftDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

        rightDrawerItems = getResources().getStringArray(R.array.right_drawer_items);
        rightDrawerListView = (ListView) findViewById(R.id.right_drawer);
        rightDrawerListView.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_listview_item, rightDrawerItems));
        rightDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }

            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_open_right_drawer:
                if (!drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Toast.makeText(DrawerActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
            drawerLayout.closeDrawer(leftDrawerListView);
            drawerLayout.closeDrawer(rightDrawerListView);
        }
    }

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private boolean scanning = false;
    private int scans = 0;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private Handler handler;
    private Map<String, Integer> rssiMap = new HashMap<String, Integer>();
    private List<String> rssiList;
    private ListView listView;

    private void initBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        listView = (ListView) findViewById(R.id.rssi_list);
        rssiList = new ArrayList<String>();
        final ArrayAdapter<String> rssiAdapter = new ArrayAdapter<String>(this, R.layout.list_rssi, rssiList);
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_rssi, rssiList));

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Globals.log("Bluetooth not available");

            rssiList.add("Bluetooth not available");
            rssiAdapter.notifyDataSetChanged();

            return;
        }

        leScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi,
                                         byte[] scanRecord) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String address = device.getAddress();
                                if (rssiMap.containsKey(address)) {
                                    rssiMap.remove(address);
                                }
                                rssiMap.put(address, rssi);
                                rssiList.clear();

                                for (String key: rssiMap.keySet()) {
                                    rssiList.add(key + ": " + rssiMap.get(key));
                                }
                                rssiAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                };

        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                    scanLeDevice(true);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
            scans++;
        } else {
            scanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            scanLeDevice(true);
        }
    }
}
