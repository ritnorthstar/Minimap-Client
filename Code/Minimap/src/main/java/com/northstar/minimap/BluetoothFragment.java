package com.northstar.minimap;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 12/9/13.
 */
public class BluetoothFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

    private boolean scanning = false;
    private double propagationConstant = 3.0;
    private int rssiAtOneMeter = -75;
    private int scans = 0;

    public static final String TAG = "RNM-BF";

    private Activity activity;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private Handler handler;
    private Map<String, Integer> rssiMap = new HashMap<String, Integer>();
    private List<String> rssiList;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_bluetooth, container, false);
        listView = (ListView) layout.findViewById(R.id.rssi_list);
        initBluetooth();

        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "Attached");
        this.activity = activity;
        initBluetooth();
    }

    private boolean initBluetooth() {
        if (activity == null || listView == null) {
            return false;
        }

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



        rssiList = new ArrayList<String>();
        final ArrayAdapter<String> rssiAdapter = new ArrayAdapter<String>(activity, R.layout.list_rssi, rssiList);
        listView.setAdapter(rssiAdapter);

        if (bluetoothAdapter.getDefaultAdapter() == null){
            rssiList.add("No bluetooth available");
            Globals.log("no bluetooth");
            rssiAdapter.notifyDataSetChanged();
            return false;
        }

        rssiList.add("RSSI LIST");

        leScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi,
                                         byte[] scanRecord) {
                        Log.d(TAG, "RSSI: " + rssi);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String address = device.getAddress();
                                if (rssiMap.containsKey(address)) {
                                    rssiMap.remove(address);
                                }
                                rssiMap.put(address, rssi);
                                rssiList.clear();

                                for (String key: rssiMap.keySet()) {
                                    int rssi = rssiMap.get(key);
                                    double distance = rssiToDistance(rssi);
                                    String distanceString = getFormattedDistance(distance);
                                    rssiList.add(key + ": " + distanceString);
                                }

                                rssiAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                };

        if (!scanning) {
            scanning = true;
            scanLeDevice();
        }

        return true;
    }

    private String getFormattedDistance(double distance) {
        return (new DecimalFormat("#.##").format(distance) + " m");
    }

    private double rssiToDistance(int rssi) {
        return Math.pow(10, (rssiAtOneMeter - rssi) / (10.0 * propagationConstant));
    }

    private void scanLeDevice() {
        // Stops scanning after a pre-defined scan period.
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            bluetoothAdapter.stopLeScan(leScanCallback);
            scanLeDevice();
            }
        }, SCAN_PERIOD);

        bluetoothAdapter.startLeScan(leScanCallback);
        Log.d(TAG, "Start scan: " + scans);
        scans++;
    }
}
