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

import com.northstar.minimap.beacon.BluetoothBeacon;
import com.northstar.minimap.beacon.IBeacon;
import com.northstar.minimap.beacon.StickNFindBluetoothBeacon;

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
    private int scans = 0;

    private Activity activity;
    private ArrayAdapter<String> beaconAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothAdapter.LeScanCallback leScanCallback;
    private BluetoothManager bluetoothManager;
    private Handler handler;
    private List<String> beaconList;
    private ListView beaconListView;
    private Map<String, IBeacon> beaconMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.fragment_bluetooth, container, false);
        beaconListView = (ListView) layout.findViewById(R.id.beacon_list);
        initBluetooth();

        beaconMap = new HashMap<String, IBeacon>();

        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        initBluetooth();
    }

    private boolean initBluetooth() {
        if (activity == null || beaconListView == null) {
            return false;
        }

        bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        beaconList = new ArrayList<String>();
        beaconAdapter = new ArrayAdapter<String>(activity, R.layout.list_beacon, beaconList);
        beaconListView.setAdapter(beaconAdapter);

        if (bluetoothAdapter.getDefaultAdapter() == null) {
            beaconList.add("No bluetooth available");
            Globals.log("no bluetooth");
            beaconAdapter.notifyDataSetChanged();
            return false;
        }

        beaconList.add("BEACON LIST");

        if (!scanning) {
            scanning = true;
            scanLeDevice();
        }

        return true;
    }
    
    private void createLeScanCallback() {
        // TODO: Remove unconnected beacons from beacon list after a certain period of time.

    	leScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi,
                                         byte[] scanRecord) {
                        Globals.log("RSSI: " + rssi);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String address = device.getAddress();

                                // Create Beacon if it doesn't exist.
                                if (!beaconMap.containsKey(address)) {
                                    beaconMap.put(address,
                                            new StickNFindBluetoothBeacon(device, null, null));
                                }

                                // Update beacon's signal strength.
                                beaconMap.get(address).setSignalStrength(rssi);
                                beaconList.clear();

                                // Update the list
                                for (String key: beaconMap.keySet()) {
                                    String distanceString =
                                            beaconMap.get(key).getFormattedDistance();
                                    beaconList.add(key + ": " + distanceString);
                                }

                                beaconAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                };
    }


    private void scanLeDevice() {
    	// Create a new LeScanCallback for every scan.
    	createLeScanCallback();
    	
        // Stops scanning after a pre-defined scan period.
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothAdapter.stopLeScan(leScanCallback);
                scanLeDevice();
            	bluetoothAdapter.stopLeScan(leScanCallback);
            	if (scanning) {
            		scanLeDevice();
            	}
            }
        }, SCAN_PERIOD);

        bluetoothAdapter.startLeScan(leScanCallback);
        Globals.log("Start scan: " + scans);
        scans++;
    }
}
