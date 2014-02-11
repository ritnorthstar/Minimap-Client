package com.northstar.minimap;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

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

    private void initBluetooth() {
        if (activity == null || listView == null) {
            return;
        }

        bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        rssiList = new ArrayList<String>();
        final ArrayAdapter<String> rssiAdapter =
                new ArrayAdapter<String>(activity, R.layout.list_rssi, rssiList);
        listView.setAdapter(rssiAdapter);
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
                                    rssiList.add(key + ": " + rssiMap.get(key));
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
